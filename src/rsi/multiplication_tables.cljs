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
    :wrongly-answered #{}
    :mode :against-the-clock}))

(defn set-deadline! []
  (set-timeout! (fn [] (swap! state assoc :deadline-passed? true))))

(defn update-mode [{:keys [wrongly-answered mode deadline-passed?] :as state} correct-anwer?]
  (cond
    (not correct-anwer?) (assoc state :mode :correct-current-question)
    (or deadline-passed? (= mode :correct-current-question)) (assoc state :mode :against-the-clock)
    (empty? wrongly-answered) (assoc state :mode :against-the-clock)
    :else (assoc state :mode :repeat-wrongly-answered)))

(defn update-question [{:keys [mode wrongly-answered] :as state}]
  (case mode
    :correct-current-question
    state
    :against-the-clock
    (-> state
        (assoc :left (random-number))
        (assoc :right (random-number)))
    :repeat-wrongly-answered
    (let [[left right] (first wrongly-answered)]
      (-> state
          (assoc :left left)
          (assoc :right right)))))

(defn update-highscore [{:keys [score highscore] :as state}]
  (assoc state :highscore (max score highscore)))

(defn update-score [{:keys [score deadline-passed?] :as state} correct-anwer?]
  (assoc state :score (if (and (not deadline-passed?) correct-anwer?) (inc score) 0)))

(defn update-wrongly-answered [{:keys [left right wrongly-answered mode deadline-passed?] :as state} correct-anwer?]
  (cond
    (= mode :correct-current-question) state
    (and correct-anwer? (not deadline-passed?)) (assoc state :wrongly-answered (disj wrongly-answered [left right]))
    (or (not correct-anwer?) deadline-passed?) (assoc state :wrongly-answered (conj wrongly-answered [left right]))
    :else state))

(defn process-answer [state answer]
  (let [{:keys [left right]} state
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
  (when (= (:mode @state) :against-the-clock)
    (set-deadline!)))

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
  (let [{:keys [score highscore left right deadline-passed?]} @state]
    [:div.app
     [score-view "Score" score]
     [score-view "High score" highscore]
     [question-view left right deadline-passed? process-answer!]]))
