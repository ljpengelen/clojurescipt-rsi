(ns rsi.largest
  (:require [reagent.core :as r]))

(defonce other-circles (r/atom []))
(defonce last-circle (r/atom nil))
(defonce previous-circle (r/atom nil))
(defonce score (r/atom 0))
(defonce high-score (r/atom 0))
(defonce clicked? (r/atom false))

(defn random-circle []
  {:x (rand-int 500)
   :y (rand-int 500)
   :r (+ 20 (rand-int 80))
   :c (str "#" (.toString (rand-int 16777215) 16))})

(comment
  (reset! score 10)
  (swap! score inc)
  (random-circle)
  @other-circles)

(defn add-circle! []
  (when @previous-circle (swap! other-circles conj @previous-circle))
  (when @last-circle (reset! previous-circle @last-circle))
  (reset! last-circle (random-circle)))

(defn score-view [label score]
  [:div label ": " score])

(comment
  (score-view "Some other label" 123))

(defn circle [{:keys [x y r c]} f]
  [:circle {:cx x
            :cy y
            :r r
            :fill c
            :on-click (fn [e] (.preventDefault e) (f))}])

(comment
  (circle {:x 123 :y 456 :r 789 :c "#ff00ff"} #()))

(defn clear-circles! []
  (reset! other-circles [])
  (reset! previous-circle nil)
  (reset! last-circle nil))

(defn game-over! []
  (reset! score 0)
  (clear-circles!))

(defn larger? [c1 c2]
  (when-not @clicked?
    (reset! clicked? true)
    (if (> (:r c1) (:r c2))
      (do
        (swap! score inc)
        (swap! high-score max @score))
      (game-over!))))

(defn next-round! []
  (if (and @previous-circle @last-circle (not @clicked?))
    (game-over!)
    (do
      (reset! clicked? false)
      (add-circle!))))

(defonce intervalId (r/atom nil))

(defn set-interval! []
  (reset! intervalId (js/setInterval next-round! 1000)))

(defn clear-interval! []
  (js/clearInterval @intervalId))

;; A Form-3 Reagent component is needed here because we
;; need to start the game loop when the component mounts
;; and stop it when it unmounts

;; https://github.com/reagent-project/reagent/blob/master/doc/CreatingReagentComponents.md

(defn app []
  (r/create-class
   {:component-did-mount set-interval!
    :component-will-unmount clear-interval!
    :reagent-render
    (fn []
      [:div
       [:p
        "Click the largest of the last two circles to score a point. "
        "If you click the wrong circle, the round is over. "
        "If you don't click a circle in time, the round is also over."]
       [score-view "Score" @score]
       [score-view "High score" @high-score]
       [:svg {:height "500"
              :width "500"}
        (for [c @other-circles]
          ^{:key (:c c)} [circle c game-over!])
        (when @previous-circle
          [circle @previous-circle
           #(larger? @previous-circle @last-circle)])
        (when @last-circle
          [circle @last-circle
           #(larger? @last-circle @previous-circle)])]])}))
