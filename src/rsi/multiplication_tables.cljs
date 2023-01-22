(ns rsi.multiplication-tables
  (:require [reagent.core :as r]))

;; Utilities

(defonce timeout-id (r/atom nil))

(defn set-timeout! [f]
  (reset! timeout-id (js/setTimeout f 4000)))

(defn clear-timeout! []
  (js/clearTimeout @timeout-id))

(defn random-number []
  (inc (rand-int 10)))

;; State

(defonce state
  (r/atom
   {:left (random-number)
    :right (random-number)
    :score 0
    :highscore 0
    :deadline-passed? false
    :wrong-answers #{}
    :mode :against-the-clock}))

(defn set-deadline! []
  (set-timeout! (fn [] (swap! state assoc :deadline-passed? true))))

(defn new-numbers! []
  (let [wrong-answers (:wrong-answers @state)]
    (swap! state assoc :deadline-passed? false)
    (when (>= (count wrong-answers) 5)
      (swap! state assoc :mode :repeat-wrong-answers))
    (when (empty? wrong-answers)
      (swap! state assoc :mode :against-the-clock))
    (if (= (:mode @state) :against-the-clock)
      (do
        (swap! state assoc :left (random-number))
        (swap! state assoc :right (random-number))
        (set-deadline!))
      (let [[new-left new-right] (first wrong-answers)]
        (swap! state assoc :left new-left)
        (swap! state assoc :right new-right)))))

(defn right-anwser! []
  (let [right (:right @state)
        left (:left @state)
        score (:score @state)]
    (swap! state update :wrong-answers disj [left right])
    (swap! state update :score inc)
    (swap! state update :highscore max score)))

(defn wrong-answer! []
  (let [right (:right @state)
        left (:left @state)]
    (swap! state assoc :score 0)
    (swap! state update :wrong-answers conj [left right])))

(comment
  @state
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
     ^{:key answer} [:li left " x " right " = " (* left right)])])

(defn app []
  (let [{:keys [score highscore left right deadline-passed? wrong-answers]} @state]
    [:div.app
     [score-view "Score" score]
     [score-view "High score" highscore]
     [question-view left right deadline-passed?
      (fn [answer]
        (clear-timeout!)
        (if (and (not deadline-passed?) (= (str (* left right)) answer))
          (right-anwser!)
          (wrong-answer!))
        (new-numbers!))]
     [wrong-answer-view wrong-answers]]))
