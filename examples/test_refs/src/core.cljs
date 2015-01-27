(ns examples.test-refs.core
  (:require com.facebook.React
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(enable-console-print!)

(def app-state
  (atom {
         :topics {
          :a ["default"]
          :b ["default"]
          :c ["default"]
         }}))
(defn add-topic-dependency [topic-map t]
  (assoc topic-map t
         (om/ref-cursor
          (get-in (om/root-cursor app-state)
                  [:topics t]))))

(defn calculate-dependencies [topics]
  (reduce add-topic-dependency {} topics)) 

(defn topics-view [topics owner]
  (reify
    om/IRender
    (render [_]
      (let [topic-cursor-map (calculate-dependencies topics)]
        (doall (map (partial om/observe owner)  (vals topic-cursor-map)))
        (dom/div nil (pr-str topic-cursor-map))))))

(defn root [app owner]
  (reify
    om/IRender
    (render [_]
      (println "Render Root")
      (dom/div nil
        (dom/div #js {:id "message"} nil)
        (om/build topics-view [:a :b])
        (dom/button
          #js {:onClick
               (fn [e]
                 (om/transact! app [:topics :a] #(conj % "New value for a") ))}
          "Change a"
          (dom/br nil))
        (dom/button
         #js {:onClick
              (fn [e]
                (om/transact! app [:topics :b] #(conj %  "New value for b") ))}
         "change b")))))

(om/root root app-state
  {:target (.getElementById js/document "app")})
