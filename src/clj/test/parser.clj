(ns test.parser
  (:refer-clojure :exclude [read])
  (:require [om.next.server :as om]
            [cheshire.core :as json]))

(def titles ["sakura" "tsubaki" "kaede"])

(def data {:flower [[0 "sakura"] [1 "tubaki"] [2 "kaede"]]
           :fruit [[0 "apple"] [1 "banana"] [2 "orange"]]
           :behicle [[0 "car"] [1 "bike"] [2 "ship"]]})


(defmulti read om/dispatch)

(defmethod read :data/title
  [{:keys [ast] :as env} k _]
  (let [params (:params ast)
        v (nth titles (:num params))]
    {:value v}))


(defmulti mutate om/dispatch)


(defmethod mutate `data/title
  [{:keys [state ast] :as env} key {:keys [val]}]
  (let []
  {:value {:data/title val}}
  ))

(defn parse-query [query]
  (let [parser (om/parser {:read read :mutate mutate})]
    (parser {} query)))
