(ns rsi.basics
  (:require [reagent.core :as r]
            [reagent.dom :as d]))

;; The basics of Reagent
;; =====================

;; Reagent atoms are used to hold state

(defonce number (r/atom 0))

;; The simplest form of Reagent components are just functions
;; (This one could have been pure if it wasn't for the print statement.)

(defn number-view [number]
  (println "Rendering")
  [:span number])

;; They can't all be pure, because some must refer to atoms

(defn app []
  [:p "The number is: " [number-view @number]])

(comment
  (d/render [app] (.getElementById js/document "app"))
  (swap! number inc) ;; Triggers a rendering each time
  (reset! number 0) ;; Triggers one rendering
  (reset! number 20)) ;; Also triggers one rendering

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
