(ns rsi.basics
  (:require [reagent.core :as r]
            [reagent.dom :as d]))

;; The basics of Reagent
;; =====================

;; Reagent atoms are used to hold state

(defonce number-of-clicks (r/atom 0))

(comment
  @number-of-clicks)

;; They can be updated directly via functions that are inherently non-pure

(comment
  (reset! number-of-clicks 10)
  (swap! number-of-clicks inc))

(defn inc-number-of-clicks! []
  (swap! number-of-clicks inc))

;; The simplest form of Reagent components are just functions

;; Those that dereference atoms are not pure

(defn number-view []
  (println "Rendering number view")
  [:p "Number of clicks: " @number-of-clicks])

(comment
  (number-view)
  (swap! number-of-clicks inc))

;; These could have been pure if it wasn't for the print statements

(defn button []
  (println "Rendering button")
  [:button {:on-click inc-number-of-clicks!} "Click me!"])

(defn app []
  (println "Rendering app")
  [:<>
   [number-view]
   [button]])

(comment
  (d/render [app] (.getElementById js/document "app"))
  (swap! number-of-clicks inc) ;; Triggers a rendering each time
  (reset! number-of-clicks 0) ;; Triggers one rendering
  (reset! number-of-clicks 20)) ;; Also triggers one rendering

(comment
  ;; Functional React components using JSX look pretty good too.
  "
  const someFunctionalReactComponent = (v1, v2) =>
  <div>
    <p>{v1}</p>
    <p>{v1 + v2}</p>
  </div>;
  "
  ;; Under the hood, however, JSX expressions compile to
  ;; function calls, which are not as straightforward to test
  ;; as simple vectors.

  ;; Compile the above using the Babel REPL to see
  ;; for yourself: https://babeljs.io/repl/
)
