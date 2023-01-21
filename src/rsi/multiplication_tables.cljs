(ns rsi.multiplication-tables
  (:require [reagent.core :as r]))

;; Utilities

(defonce timeout-id (r/atom nil))

(defn set-timeout! [f]
  (reset! timeout-id (js/setTimeout f 4000)))

(defn clear-timeout! []
  (js/clearTimeout @timeout-id))

;; State

(defn random-number []
  (inc (rand-int 10)))

(defonce score (r/atom 0))
(defonce highscore (r/atom 0))

(defonce left (r/atom (random-number)))
(defonce right (r/atom (random-number)))

(defonce deadline-passed? (r/atom false))

(defonce wrong-answers (r/atom #{}))

(defonce mode (r/atom :against-the-clock))

(defn set-deadline! []
  (set-timeout! (fn [] (reset! deadline-passed? true))))

(defn new-numbers! []
  (reset! deadline-passed? false)
  (when (>= (count @wrong-answers) 5)
    (reset! mode :repeat-wrong-answers))
  (when (empty? @wrong-answers)
    (reset! mode :against-the-clock))
  (if (= @mode :against-the-clock)
    (do
      (reset! left (random-number))
      (reset! right (random-number))
      (set-deadline!))
    (let [[new-left new-right] (first @wrong-answers)]
      (reset! left new-left)
      (reset! right new-right))))

(defn right-anwser! []
  (swap! wrong-answers disj [@left @right])
  (swap! score inc)
  (swap! highscore max @score))

(defn wrong-answer! []
  (reset! score 0)
  (swap! wrong-answers conj [@left @right]))

(comment
  (right-anwser!)
  (wrong-answer!)
  (new-numbers!))

;; Reagent components

(defn score-view [label score]
  [:div (str label ": " score)])

(defn question-view []
  (let [value (r/atom "")]
    (fn [left right deadline-passed? on-submit]
      [:div (when deadline-passed? {:class "deadline-passed"}) (str left " x " right " = ")
       [:form {:on-submit (fn [e]
                            (.preventDefault e)
                            (on-submit @value)
                            (reset! value ""))}
        [:input {:type "text"
                 :inputmode "numeric"
                 :value @value
                 :on-change (fn [e]
                              (reset! value (.. e -target -value)))}]]])))

(defn wrong-answer-view [wrong-answers]
  [:ul
   (for [[left right :as answer] wrong-answers]
     ^{:key answer} [:li left " x " right])])

;; A Form-3 Reagent component is needed here because we
;; need to start the game loop when the component mounts
;; and stop it when it unmounts

;; https://github.com/reagent-project/reagent/blob/master/doc/CreatingReagentComponents.md

(defn app []
  (r/create-class
   {:component-did-mount set-deadline!
    :component-will-unmount clear-timeout!
    :reagent-render
    (fn []
      [:div.app
       [score-view "Score" @score]
       [score-view "High score" @highscore]
       [question-view @left @right @deadline-passed?
        (fn [answer]
          (clear-timeout!)
          (if (and (not @deadline-passed?) (= (str (* @left @right)) answer))
            (right-anwser!)
            (wrong-answer!))
          (new-numbers!))]
       [wrong-answer-view @wrong-answers]])}))
