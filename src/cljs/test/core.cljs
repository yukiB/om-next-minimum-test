(ns test.core
  (:require [cljs-http.client :as http]
            [sablono.core :refer-macros [html]]
            [om.next :as om :refer-macros [defui]]
            [cljs.core.async :refer [<! timeout]]
            [cognitect.transit :as transit]
            [om.next :as om]
            [goog.dom :as gdom]))

(enable-console-print!)

(def app-state (atom {}))

(defn get-data [state key]
  (let [st @state]
    (into [] (map #(get-in st %)) (get st key))))

(defmulti read om/dispatch)

(defmethod read :data/title
  [{:keys [state ast] :as env} key params]
  (let [st @state
        _ (print "read" key ast)]
    (if (contains? st key)
      {:value (get-data state key)}
      {:remote ast})))

(defmulti mutate om/dispatch)

(def reconciler
  (om/reconciler
    {:state  app-state
     :normalize true
     :parser (om/parser {:read read :mutate mutate})
     :send   (fn [{query :remote} callback]
               (let [_ (print "send")
                     _ (print query)]
                 (http/post "/api/query"
                            {:params query})))
     }))

(defonce mounted (atom false))

(defui RootView
  static om/IQuery
  (query [this]
         `[:data/title])
  Object
  (render [this]
          (let [{:keys [data/title]} (om/props this)]
          (html
           [:div "title"]))))


(defn init! []
  (let [target (gdom/getElement "main")]
      (om/add-root! reconciler RootView target)
      (reset! mounted false)))
