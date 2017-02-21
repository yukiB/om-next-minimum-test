(ns test.page
  (:use [hiccup core page element])
  (:require [test.config :as config]))

(def favicon
  [:link {:rel "shortcut icon" :href "/favicon.ico"}])

(defn head
  []
  [:head
   "<!--[if lt IE 9]>"
   (include-js "/js/html5.js")
   "<![endif]-->"
   [:title "om next minimum test"]
   [:meta {:charset "utf-8"}]
   [:meta {:name "title" :content "Todai Onco Panel Report"}]
   [:meta {:name "keywords" :content ""}]
   [:meta {:name "description" :content ""}]
   [:meta {:name "viewport" :content "width=device-width"}]
   [:meta {:name "format-detection" :content "telephone=no"}]
   [:script {:src "/js/main.js"}]
   favicon])

(defn frame
  [& {:keys [contents page-class]
      :or {contents nil
           page-class :main}}]
  (let [body (vec (concat [:body] contents))]
    (html5
     {}
     (head)
     body)))

(defn view
  [req]
    (frame
     :contents (html
                [:header
                 [:h1 [:a {:href "/"}]]]
                [:div#main])))
