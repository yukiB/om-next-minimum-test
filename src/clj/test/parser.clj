(ns test.parser
  (:refer-clojure :exclude [read])
  (:require [om.next.server :as om]
            [cheshire.core :as json]))


(defn json-response
  [data]
  {:status 200
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body (json/generate-string data)})

(defn data-title
  []
  (-> {:value "Title"}
      json-response))

(defmulti read om/dispatch)

(defmethod read :patients/list
  [env k _]
  (let [v (data-title)]
    v))

(defmulti mutate om/dispatch)

(defn parse-query [query]
  (let [parser (om/parser {:read read :mutate mutate})]
    (parser {} query)))
