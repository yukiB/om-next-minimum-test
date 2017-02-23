(ns test.parser
  (:refer-clojure :exclude [read])
  (:require [om.next.server :as om]
            [cheshire.core :as json]))

(def titles ["python" "clojure" "c++"])

(def data {:flower [[0 "sakura"] [1 "tubaki"] [2 "kaede"]]
           :fruit [[0 "apple"] [1 "banana"] [2 "orange"]]
           :behicle [[0 "car"] [1 "bike"] [2 "ship"]]})


(defmulti read om/dispatch)

(defmethod read :data/title
  [{:keys [ast] :as env} k _]
  (let [params (:params ast)
        _ (.println System/out "TITLE")
        _ (.println System/out ast)
        v (nth titles (:id params))
        _ (.println System/out v)]
    {:value v}))

(defmethod read :data/cat
  [{:keys [ast] :as env} k _]
  (let [n (get-in ast [:params :id])
        v (nth (keys data) n)]
    {:value {:id n :name (name v)}}))

(defmethod read :data/cats
  [{:keys [ast] :as env} k _]
  (let [_ (.println System/out "data/cats")]
    {:value (map-indexed (fn [idx itm] {:cat/id idx :cat/name itm}) (keys data))}))

(defmulti mutate om/dispatch)


(defmethod mutate `data/title
  [{:keys [state ast] :as env} key {:keys [val]}]
  (let []
  {:value {:data/title (str "input: " val)}}
  ))

(defn parse-query [query]
  (let [parser (om/parser {:read read :mutate mutate})]
    (parser {} query)))
