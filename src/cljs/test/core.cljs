(ns test.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [sablono.core :refer-macros [html]]
            [om.next :as om :refer-macros [defui]]
            [cljs.core.async :refer [<! timeout chan put!]]
            [cognitect.transit :as transit]
            [om.next :as om]
            [goog.dom :as gdom]))

(enable-console-print!)


(def app-state (atom {}))

(defn get-data [state key]
  (let [st @state]
    (into [] (map #(get-in st %)) (get st key))))

(defmulti read om/dispatch)

(defmethod read :data/input
  [{:keys [state ast] :as env} key params]
  {:value (key @state)})

(defmethod read :data/title
  [{:keys [state ast] :as env} key params]
  (let [st @state
        _ (print st)]
    (if (contains? st key) 
      {:value (key st)}
      {:remote ast})))

(defmethod read :data/cat
  [{:keys [state ast] :as env} key params]
  (let [st @state]
    (if (contains? st key) 
      {:value (key st)}
      {:remote ast})))

(defmethod read :selected
  [{:keys [state ast] :as env} key params]
  (when (contains? @state key) 
    {:value (key @state)}))

(defmethod read :data/cats
  [{:keys [state ast] :as env} key params]
  (let [st @state
        _ (print "cats" key)]
    (if (contains? st key) 
      {:value (key st)}
      {:remote ast})))

(defmulti mutate om/dispatch)

(defmethod mutate `data/title
  [{:keys [state ast] :as env} key {:keys [val]}]
  {:remote ast})

(defmethod mutate `select/cat
  [{:keys [state ast] :as env} key {:keys [val]}]
  (let [_ (println val)]
  (merge
   {:action
    (swap! state update-in [:data/selected]
           #(set (assoc {} :cat/id val)))}
   (when (seq val) {:remote ast}))))

(def reconciler
  (om/reconciler
    {:state  app-state
     :normalize false
     :parser (om/parser {:read read :mutate mutate})
     :send   (fn [{query :remote} callback]
               (let [{[search] :children} (om/query->ast query)
                     _ (print "search:" search)]
                 (go 
                   (let [result (<! (http/post "/api/query" 
                                               {:transit-params query}))
                         data (:body result)
                         k (:key search)
                         read? (instance? cljs.core/Keyword k)]
                     (callback (if read? data (k data)))))))}))

(defonce mounted (atom false))

(defn change-input [e this]
  (om/transact! this `[(data/title {:val ~(.. e -target -value)})]))

(defn change-cat [e this]
  (om/transact! this `[(select/cat {:val ~(.. e -target -value)})]))

(defui CatOption
  static om/IQuery
  (query [this]
         `[:cat/id :cat/name])
  Object
  (render
   [this]
   (let [{:keys [cat/id cat/name] :as props} (om/props this)]
     (html
      [:option {:value id} name]))))

(def cat-option (om/factory CatOption))


(defui SelectView
  Object
  (render
   [this]
   (let [{:keys [cats cat] :as props} (om/props this)]
    (html
     [:div
      [:select {:on-change #(change-cat % this)}
       (map #(cat-option %) cats)]
      ]))))

(def select-view (om/factory SelectView))

(defui RootView
  static om/IQuery
  (query [this]
         (let []
         `[(:data/title {:id 0})
           :selected
           :data/input
           :data/cats]))
  Object
  (render
   [this]
   (let [{:keys [data/cats data/title selected data/input] :as props} (om/props this)
         _ (println "cat" selected)]
      (html
       [:div
        [:input {:type "text"
                 :value input
                 :on-change (fn [e] (change-input e this))}]
        (select-view {:cat cat :cats cats})
        [:h1 title]
        [:h2 ]
        [:h3 ]
        ]))))

(defn init! []
  (let [target (gdom/getElement "main")]
      (om/add-root! reconciler RootView target)
      (reset! mounted false)))
