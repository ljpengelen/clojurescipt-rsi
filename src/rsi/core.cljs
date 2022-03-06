(ns rsi.core
  (:require [reagent.core :as r]
            [reagent.dom :as d]
            [rsi.basics :as basics]
            [rsi.largest :as largest]
            [rsi.multiplication-tables :as multiplication-tables]
            [rsi.paint :as paint]
            [rsi.second-largest :as second-largest]))

;; The state of the app

(defonce selected-page (r/atom nil))

;; State manipulation

(defn select-page! [page]
  (reset! selected-page page))

;; Reagent components

(defn page-selector [page label]
  [:button {:on-click #(select-page! page)} label])

(defn app-selector []
  [:<>
   [page-selector :basics "Reagent Basics"]
   [page-selector :largest "Click the largest circle"]
   [page-selector :multiplication-tables "Multiplication tables"]
   [page-selector :paint "Paint"]
   [page-selector :second-largest "Click the second largest circle"]])

;; Main app component

(defn app []
  (case @selected-page
    :basics [basics/app]
    :largest [largest/app]
    :multiplication-tables [multiplication-tables/app]
    :paint [paint/app]
    :second-largest [second-largest/app]
    [app-selector]))

;; Attach the main app component to a DOM element

(defn mount-root []
  (d/render [app] (.getElementById js/document "app")))

;; Initialization at the start of the app

(defn ^:export init! []
  (mount-root))
