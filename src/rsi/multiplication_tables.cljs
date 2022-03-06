(ns rsi.multiplication-tables
  (:require [reagent.core :as r]))

(defonce score (r/atom 0))
(defonce highscore (r/atom 0))

(defonce left (r/atom (rand-int 11)))
(defonce right (r/atom (rand-int 11)))
(defonce answer (r/atom ""))

(defn new-numbers! []
  (reset! left (rand-int 11))
  (reset! right (rand-int 11))
  (reset! answer ""))

(defn win! []
  (swap! score inc)
  (swap! highscore max @score)
  (new-numbers!))

(defn lose! []
  (reset! score 0)
  (new-numbers!))

(comment
  (win!)
  (lose!)
  (new-numbers!))

(defn score-view [label score]
  [:div (str label ": " score)])

(defn question-view [left right answer on-change on-submit]
  [:div (str left " x " right " = ")
   [:input {:type "text"
            :value answer
            :on-change (fn [e]
                         (let [value (.. e -target -value)]
                           (on-change value)))
            :on-key-press (fn [e]
                            (when (= (.-key e) "Enter")
                              (on-submit (.. e -target -value))))}]])

(defn app []
  [:div.app
   [score-view "Score" @score]
   [score-view "High score" @highscore]
   [question-view @left @right @answer
    (fn [new-answer]
      (reset! answer new-answer))
    (fn [answer]
      (if (= (str (* @left @right)) answer)
        (win!)
        (lose!)))]])
