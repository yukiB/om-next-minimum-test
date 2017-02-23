(ns test.parser
  (:refer-clojure :exclude [read])
  (:require [om.next.server :as om]
            [cheshire.core :as json]))

(def titles ["test1" "test2" "test3"])

(def data {:list [{:id 0
                   :name "flower"
                   :types [{:id 0 :name "sakura"} {:id 1 :name "tsubaki"} {:id 2 :name "kaede"}]}
                  {:id 1
                   :name "fuit"
                   :types [{:id 0 :name "apple"} {:id 1 :name "banana"} {:id 2 :name "orange"}]}
                  {:id 2
                   :name "behicle"
                   :types [{:id 0 :name "car"} {:id 1 :name "bike"} {:id 2 :name "ship"}]}]})


(defmulti read om/dispatch)

(defmethod read :data/title
  [{:keys [ast] :as env} k _]
  (let [params (:params ast)
        v (nth titles (:id params))]
    {:value v}))

(defmethod read :data/cat
  [{:keys [ast] :as env} k _]
  (let [n (get-in ast [:params :id])
        v (nth (keys data) n)]
    {:value {:id n :name (name v)}}))

(defmethod read :data/cats
  [{:keys [ast] :as env} k _]
  (let [_ (.println System/out "data/cats")]
    {:value (map (fn [x] {:cat/id (:id x) :cat/name (:name x)}) (:list data))}))

(defmulti mutate om/dispatch)


(defmethod mutate `data/title
  [{:keys [state ast] :as env} key {:keys [val]}]
  {:value {:data/title (str "input: " val)}})

(defmethod mutate `select/cat
  [{:keys [state ast] :as env} key {:keys [val]}]
   {:value {:data/cats (str "input: " val)}})

(defn parse-query [query]
  (let [parser (om/parser {:read read :mutate mutate})]
    (parser {} query)))
