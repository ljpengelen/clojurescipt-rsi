(ns rsi.core
  (:require [reagent.core :as r]
            [reagent.dom :as d]
            [rsi.basics :as basics]
            [rsi.largest :as largest]
            [rsi.multiplication-tables :as multiplication-tables]
            [rsi.paint :as paint]
            [rsi.second-largest :as second-largest]))

;; Store and update the current fragment identifier

(defonce fragment-identifier (r/atom js/window.location.hash))

(defn handle-hash-change! [_]
  (js/console.log "hash changed")
  (reset! fragment-identifier js/window.location.hash))

(defn handle-hash-changes! []
  (js/console.log "listening to hash changes")
  (js/addEventListener "hashchange" handle-hash-change!))

;; Reagent components

(defn app-link [url label]
  [:li [:a {:href url} label]])

(defn app-selector []
  [:ul
   [app-link "#/basics" "Reagent Basics"]
   [app-link "#/largest" "Click the largest circle"]
   [app-link "#/multiplication-tables" "Multiplication tables"]
   [app-link "#/paint" "Paint"]
   [app-link "#/second-largest" "Click the second largest circle"]])

;; Main app component

(defn app [hash]
  (case @hash
    "#/basics" [basics/app]
    "#/largest" [largest/app]
    "#/multiplication-tables" [multiplication-tables/app]
    "#/paint" [paint/app]
    "#/second-largest" [second-largest/app]
    [app-selector]))

;; Attach the main app component to a DOM element

(defn mount-root []
  (d/render [app fragment-identifier] (.getElementById js/document "app")))

;; Initialization at the start of the app

(defn ^:export init! []
  (handle-hash-changes!)
  (mount-root))
