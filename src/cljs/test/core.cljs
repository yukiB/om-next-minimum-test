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
        _ (print "read" key st)]
    (if (contains? st key) 
      {:value (key st)}
      {:remote ast})))


(defmulti mutate om/dispatch)

(defmethod mutate `data/title
  [{:keys [state ast] :as env} key {:keys [val]}]
  (let [_ (print "--------")
        _ (print val)
        _ (print ast)
        _ (print "--------")]
  (merge
   {:action
    (swap! state assoc-in [:data/title] "changed")}
;;    (fn []
;;      (swap! state update-in [:data/title] #(let [_ (print "change"  %)
;;                                                  _ (print ast)]
   ;;                                              (set [:data/by-input input]))))}
   
   (when (seq val) {:remote ast}))))



(def reconciler
  (om/reconciler
    {:state  app-state
     :normalize false
     :parser (om/parser {:read read :mutate mutate})
     :send   (fn [{query :remote} callback]
               (let [;;_ (print "send" query)
                     {[search] :children} (om/query->ast query)
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
  (print "change")
  (om/transact! this `[(data/title {:val ~(.. e -target -value)})]))





(defui SelectView
  Object
  (render [this]
          ))
(def select-view (om/factory SelectView))

(defui RootView
  static om/IQuery
  (query [this]
         `[(:data/title {:num 0}) :data/input])
  Object
  (render [this]
    (let [{:keys [data/title data/input] :as props} (om/props this)]
      (html
       [:div
        [:input {:type "text"
                 :value input
                 :on-change (fn [e] (change-input e this))}]
        [:h1 title]
        ]))))

(defn init! []
  (let [target (gdom/getElement "main")]
      (om/add-root! reconciler RootView target)
      (reset! mounted false)))
