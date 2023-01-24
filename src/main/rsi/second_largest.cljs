(ns rsi.second-largest
  (:require [reagent.core :as r]))

(defonce score (r/atom 0))
(defonce highscore (r/atom 0))

(defonce circles (r/atom []))

(defonce next-colors (r/atom (cycle ["cyan" "magenta" "yellow" "black"])))
(defonce color (r/atom "black"))

(defn next-color! []
  (reset! color (first @next-colors))
  (swap! next-colors next))

(defn random-circle []
  {:x (rand-int 500) :y (rand-int 500) :r (rand-int 100)})

(defn new-circles! []
  (reset! circles (repeatedly 5 random-circle)))

(comment
  (reset! score 21)
  (swap! score inc)
  (new-circles!))

(defn score-view [label score]
  [:div (str label ": " @score)])

(defonce timeoutId (r/atom nil))

(defn set-timeout! [f]
  (reset! timeoutId (js/setTimeout f 1500)))

(defn clear-timeout! []
  (js/clearTimeout @timeoutId))

(defn game-over! []
  (clear-timeout!)
  (next-color!)
  (reset! score 0)
  (new-circles!)
  (set-timeout! game-over!))

(defn score! []
  (clear-timeout!)
  (swap! score inc)
  (swap! highscore max @score)
  (new-circles!)
  (set-timeout! game-over!))

(comment
  (next-color!))

(defn circle [{:keys [x y r]} max?]
  [:circle {:cx x
            :cy y
            :r r
            :fill @color
            :on-click (fn [e]
                        (.stopPropagation e)
                        (if max?
                          (score!)
                          (game-over!)))}])

(defn second-largest [circles]
  (->> circles
       (map :r)
       (sort >)
       rest
       first))

(defn is-second-largest? [r circles]
  (= r (second-largest circles)))

(comment
  (second-largest [{:r 10} {:r 50} {:r 100} {:r 99}]))

;; A Form-3 Reagent component is needed here because we
;; need to start the game loop when the component mounts
;; and stop it when it unmounts

;; https://github.com/reagent-project/reagent/blob/master/doc/CreatingReagentComponents.md

(defn app []
  (r/create-class
   {:component-did-mount (fn [_] (new-circles!) (set-timeout! game-over!))
    :component-will-unmount clear-timeout!
    :reagent-render
    (fn [] [:div.app
            [:p
             "Click the second largest circle to score a point. "
             "If you click the wrong circle, the round is over. "
             "If you don't click a circle on time, the round is over too."]
            [score-view "Score" score]
            [score-view "High score" highscore]
            (let [circles @circles]
              [:svg {:height "500"
                     :width "500"
                     :on-click #(game-over!)}
               (for [c circles]
                 ^{:key c} [circle c (is-second-largest? (:r c) circles)])])])}))
