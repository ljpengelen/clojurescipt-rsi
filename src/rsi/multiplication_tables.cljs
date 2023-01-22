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

(defn update-mode [{:keys [wrongly-answered] :as state}]
  (cond-> state
    (empty? wrongly-answered) (assoc :mode :against-the-clock)
    (>= (count wrongly-answered) 5) (assoc :mode :repeat-wrongly-answered)))

(defn update-question [{:keys [mode wrongly-answered] :as state}]
  (if (= mode :against-the-clock)
    (-> state
        (assoc :left (random-number))
        (assoc :right (random-number)))
    (let [[left right] (first wrongly-answered)]
      (-> state
          (assoc :left left)
          (assoc :right right)))))

(defn update-highscore [{:keys [score highscore] :as state}]
  (assoc state :highscore (max score highscore)))

(defn update-score [{:keys [score] :as state} correct-anwer?]
  (assoc state :score (if correct-anwer? (inc score) 0)))

(defn update-wrongly-answered [{:keys [left right wrongly-answered] :as state} correct-anwer?]
  (assoc state :wrongly-answered (if correct-anwer?
                                   (disj wrongly-answered [left right])
                                   (conj wrongly-answered [left right]))))

(defn process-answer [state answer]
  (let [{:keys [left right deadline-passed?]} state
        correct-anwer? (and (not deadline-passed?) (= (str (* left right)) answer))]
    (-> state
        update-highscore
        (update-score correct-anwer?)
        (update-wrongly-answered correct-anwer?)
        update-mode
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

(defn wrongly-answered-view [wrongly-answered]
  [:ul
   (for [[left right :as answer] wrongly-answered]
     ^{:key answer} [:li left " x " right " = " (* left right)])])

(defn app []
  (let [{:keys [score highscore left right deadline-passed? wrongly-answered]} @state]
    [:div.app
     [score-view "Score" score]
     [score-view "High score" highscore]
     [question-view left right deadline-passed? process-answer!]
     [wrongly-answered-view wrongly-answered]]))
