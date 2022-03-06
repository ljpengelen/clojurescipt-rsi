(ns rsi.core
  (:require [reagent.dom :as d]
            [rsi.basics :as basics]
            [rsi.largest :as largest]
            [rsi.multiplication-tables :as multiplication-tables]
            [rsi.paint :as paint]
            [rsi.second-largest :as second-largest]))

;; Reagent components

(defn app-link [url label]
  [:li [:a {:href url} label]])

(defn app-selector []
  [:ul
   [app-link "/basics" "Reagent Basics"]
   [app-link "/largest" "Click the largest circle"]
   [app-link "/multiplication-tables" "Multiplication tables"]
   [app-link "/paint" "Paint"]
   [app-link "/second-largest" "Click the second largest circle"]])

;; Main app component

;; A true SPA would perform navigation without triggering
;; page reloads. This simple approach is good enough
;; for this app.

(defn app []
  (case js/window.location.pathname
    "/basics" [basics/app]
    "/largest" [largest/app]
    "/multiplication-tables" [multiplication-tables/app]
    "/paint" [paint/app]
    "/second-largest" [second-largest/app]
    [app-selector]))

;; Attach the main app component to a DOM element

(defn mount-root []
  (d/render [app] (.getElementById js/document "app")))

;; Initialization at the start of the app

(defn ^:export init! []
  (mount-root))
