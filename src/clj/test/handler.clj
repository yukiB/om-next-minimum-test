(ns test.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [test.parser :as parser]
            [test.page :as page]))

(defroutes app-routes
  (GET "/" req (page/view req))
  (POST "/api/query" req (parser/data-title))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes (assoc-in site-defaults [:security :anti-forgery] false)))

(defn init!
  [])

(defn halt!
  [])
