(ns rsi.core
  (:require [reagent.core :as r]
            [reagent.dom :as d]))

(defonce circles (r/atom {}))
(defonce mode (r/atom :recolor))

(defn circle
  ([x y c] (circle x y c 10))
  ([x y c r] {:x x :y y :c c :r r}))

(comment
  (reset! circles {}))

(defn grow [{:keys [x y c r]}]
  (circle x y c (* 2 r)))

(defn shrink [circle]
  (update circle :r #(/ % 2)))

(defn random-color []
  (str "#" (.toString (rand-int 16777215) 16)))

(defn recolor [c]
  (assoc c :c (random-color)))

(defn recolor-all [cs]
  (reduce-kv (fn [m k v] (assoc m k (recolor v))) {} cs))

(comment
  (random-color)
  (circle 123 456 "#ff00ff")
  (grow (circle 123 456 "#ffff00"))
  (shrink (circle 123 456 "#fff"))
  (recolor (circle 123 456 "#fff"))
  (recolor-all {"abc" (circle 123 456 "#fff")
                "def" (circle 123 456 "#fff")}))

(defn svg-circle [{:keys [x y r c]} on-click]
  [:circle {:cx x
            :cy y
            :r r
            :fill c
            :on-click on-click}])

(defn add-circle! [x y]
  (let [color (random-color)
        new-circle (circle x y color)]
    (swap! circles assoc color new-circle)))

(def modification-functions {:recolor recolor
                            :grow grow
                            :shrink shrink})

(defn modify-circle! [key]
  (let [f (get modification-functions @mode)]
    (swap! circles update key f)))

(defn coords [e]
  (let [rect (.. e -target getBoundingClientRect)
        x (- (.-clientX e) (.-left rect))
        y (- (.-clientY e) (.-top rect))]
    [x y]))

(defn app []
  [:div
   [:svg {:height "500"
          :width "500"
          :on-click (fn [e] (let [[x y] (coords e)] (add-circle! x y)))}
    (for [[key circle] @circles]
      ^{:key key} [svg-circle circle (fn [e] (.stopPropagation e) (modify-circle! key))])]
   [:button {:on-click #(reset! mode :recolor)} "Change color"]
   [:button {:on-click #(reset! mode :grow)} "Grow"]
   [:button {:on-click #(reset! mode :shrink)} "Shrink"]])

(defn mount-root []
  (d/render [app] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
