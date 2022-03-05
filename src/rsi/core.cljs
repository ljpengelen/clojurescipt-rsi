(ns rsi.core
  (:require [flatland.ordered.map :refer [ordered-map]]
            [reagent.core :as r]
            [reagent.dom :as d]))

;; The state of the app

(defonce circles (r/atom (ordered-map)))
(defonce mode (r/atom :recolor))

(comment
  @circles
  @mode
  (reset! circles (ordered-map)))

;; Functions to create circles and colors

(defn
  circle
  "Returns a map representing a circle with center (x, y),
   color c, and radius r.
   Radius r is optional and defaults to 10."
  ([x y c] (circle x y c 10))
  ([x y c r] {:x x :y y :c c :r r}))

(comment
  (circle 10 20 "#fff" 30))

(defn random-color
  "Returns a random color in hexadecimal notation."
  [] (str "#" (.toString (rand-int 16777215) 16)))

(comment
  (random-color))

;; Functions to manipulate circles

(defn grow [{:keys [x y c r]}]
  (circle x y c (* 1.1 r)))

(comment
  (grow (circle 123 456 "#ffff00" 1)))

(defn shrink [circle]
  (update circle :r #(* 0.9 %)))

(comment
  (shrink (circle 123 456 "#fff" 4)))

(defn recolor [c]
  (assoc c :c (random-color)))

(comment
  (recolor (circle 123 456 "#fff")))

(defn recolor-all [cs]
  (reduce-kv (fn [m k v] (assoc m k (recolor v))) (ordered-map) cs))

(comment
  (recolor-all {"abc" (circle 123 456 "#fff")
                "def" (circle 123 456 "#fff")}))

;; Reagent components

(defn svg-circle
  "Takes a map of a circle and an on-click function,
   and returns an SVG circle."
  [{:keys [x y r c]} on-click]
  [:circle {:cx x
            :cy y
            :r r
            :fill c
            :on-click on-click}])

(comment
  (svg-circle (circle 12 34 "#00f") identity)
  
  ;; Function are only equal to themselves
  
  (= identity identity)
  (= svg-circle svg-circle)
  (= identity (fn [x] x))
  (= (fn [x] x) (fn [x] x)))

(defn change-mode-button [current-mode new-mode label on-click]
  (let [style (when (= current-mode new-mode) {:font-weight "bold"})]
    [:button {:on-click on-click
              :style style} label]))

(comment
  (change-mode-button :recolor :recolor "Change color" identity)
  (change-mode-button :recolor :shrink "Shrink" identity))

;; Functions to manipulate the global state

(defn add-circle!
  ([x y] (add-circle! x y (random-color)))
  ([x y c]
   (let [new-circle (circle x y c)]
     (swap! circles assoc c new-circle))))

(comment
  (add-circle! (rand-int 500) (rand-int 500))
  (swap! circles recolor-all))

(defn set-mode! [new-mode]
  (reset! mode new-mode))

(comment
  (set-mode! :recolor)
  (set-mode! :grow)
  (set-mode! :shrink))

(def modification-functions {:recolor recolor
                             :grow grow
                             :shrink shrink})

(defn modify-circle! [key]
  (let [f (get modification-functions @mode)]
    (swap! circles update key f)))

(comment
  (add-circle! (rand-int 500) (rand-int 500) "#00f")
  (do
    (set-mode! :recolor)
    (modify-circle! "#00f"))
  (do
    (set-mode! :grow)
    (modify-circle! "#00f"))
  (do
    (set-mode! :shrink)
    (modify-circle! "#00f")))

;; Utility function

(defn coords
  "Returns the coordinates of e relative to its parent."
  [e]
  (let [rect (.. e -target getBoundingClientRect)
        x (- (.-clientX e) (.-left rect))
        y (- (.-clientY e) (.-top rect))]
    [x y]))

;; Main app component

(defn app []
  [:div
   [:svg {:height "500"
          :width "500"
          :on-click (fn [e] (let [[x y] (coords e)] (add-circle! x y)))}
    (for [[key circle] @circles]
      ^{:key key} [svg-circle circle (fn [e] (.stopPropagation e) (modify-circle! key))])]
   [:div
    [change-mode-button @mode :recolor "Change color" #(set-mode! :recolor)]
    [change-mode-button @mode :grow "Grow" #(set-mode! :grow)]
    [change-mode-button @mode :shrink "Shrink" #(set-mode! :shrink)]]
   [:button {:on-click #(swap! circles recolor-all)} "Change all colors"]])

;; Attach the main Reagent component to a DOM element

(defn mount-root []
  (d/render [app] (.getElementById js/document "app")))

;; Initialization at the start of the app

(defn ^:export init! []
  (mount-root))















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

(defn number-app []
  [:p "The number is: " [number-view @number]])

(comment
  (d/render [number-app] (.getElementById js/document "app"))
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

  ;; Compile the above using the Babel REPL: https://babeljs.io/repl/
  )
