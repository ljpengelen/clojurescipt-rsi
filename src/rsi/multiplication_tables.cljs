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

(defn random-question []
  [(random-number) (random-number)])

;; State

(defonce state
  (r/atom
   {:question (random-question)
    :score 0
    :highscore 0
    :deadline-passed? false
    :wrongly-answered #{}
    :mode :against-the-clock}))

(defn set-deadline! []
  (set-timeout! (fn [] (swap! state assoc :deadline-passed? true))))

(defn update-mode [{:keys [mode wrongly-answered] :as state} correct-answer?]
  (cond
    (not correct-answer?) (assoc state :mode :correct-current-question)
    (and (= mode :against-the-clock) (seq wrongly-answered)) (assoc state :mode :repeat-wrongly-answered) 
    (not= mode :against-the-clock) (assoc state :mode :against-the-clock)
    :else state))

(defn update-question [{:keys [mode wrongly-answered] :as state}]
  (cond
    (= mode :correct-current-question) state
    (= mode :against-the-clock) (assoc state :question (random-question))
    :else (let [wrong-answer (first wrongly-answered)] (-> state
                                                           (assoc :question wrong-answer)
                                                           (update :wrongly-answered disj wrong-answer)))))

(defn update-highscore [{:keys [score highscore] :as state}]
  (assoc state :highscore (max score highscore)))

(defn update-score [{:keys [score deadline-passed?] :as state} correct-anwer?]
  (assoc state :score (if (and (not deadline-passed?) correct-anwer?) (inc score) 0)))

(defn update-wrongly-answered [{:keys [deadline-passed? question wrongly-answered] :as state} correct-answer?]
  (if (or (not correct-answer?) deadline-passed?)
    (assoc state :wrongly-answered (conj wrongly-answered question))
    state))

(defn process-answer [state answer]
  (let [[left right] (:question state)
        correct-anwer? (= (str (* left right)) answer)]
    (-> state
        (update-score correct-anwer?)
        update-highscore
        (update-wrongly-answered correct-anwer?)
        (update-mode correct-anwer?)
        update-question
        (assoc :deadline-passed? false))))

(defn process-answer! [answer]
  (clear-timeout!)
  (swap! state process-answer answer)
  (set-deadline!))

(comment
  @state)

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
                 :inputMode "numeric"
                 :value @value
                 :on-change (fn [e]
                              (reset! value (.. e -target -value)))}]]])))

(defn app []
  (let [{:keys [score highscore question deadline-passed?]} @state
        [left right] question]
    [:div.app
     [score-view "Score" score]
     [score-view "High score" highscore]
     [question-view left right deadline-passed? process-answer!]]))
