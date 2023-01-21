(ns rsi.multiplication-tables
  (:require [reagent.core :as r]))

(defn random-number []
  (inc (rand-int 10)))

(defonce score (r/atom 0))
(defonce highscore (r/atom 0))

(defonce left (r/atom (random-number)))
(defonce right (r/atom (random-number)))
(defonce answer (r/atom ""))

(defn new-numbers! []
  (reset! left (random-number))
  (reset! right (random-number))
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

(defn question-view []
  (let [value (r/atom "")]
    (fn [left right on-submit]
      [:div (str left " x " right " = ")
       [:form {:on-submit (fn [_]
                            (on-submit @value)
                            (reset! value ""))}
        [:input {:type "text"
                 :value @value
                 :on-change (fn [e]
                              (reset! value (.. e -target -value)))}]]])))

(defn app []
  [:div.app
   [score-view "Score" @score]
   [score-view "High score" @highscore]
   [question-view @left @right
    (fn [answer]
      (if (= (str (* @left @right)) answer)
        (win!)
        (lose!)))]])
