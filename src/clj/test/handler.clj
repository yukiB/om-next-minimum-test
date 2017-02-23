(ns test.handler
  (:require [clojure.tools.logging :as log]
            [compojure.core :refer :all]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [slingshot.slingshot :refer [try+]]
            [bidi.ring :refer [make-handler]]
            [ring.util.response :as res]
            [test.parser :as parser]
            [test.route :as route]
            [test.page :as page]))

(defn error-body
  [s]
  {:error {:message s}})

(defn query-handler
  [params]
  (.println System/out (:body-params params))
  (res/response (parser/parse-query (:body-params params))))


(def api-routes {"/query" {:post query-handler}})
  
(def route-pack ["/" (assoc route/view-routes
                        "api" api-routes)])

(defmulti page-root identity)

(defmethod page-root :default [_] #(page/view %))

(defn- handle-route
  [route]
  (if (keyword? route)
    (page-root route)
    (identity route)))
  
(defn wrap-exception
  [handler uri-re]
  (fn [request]
    (if (re-find uri-re (:uri request))
      (try+
        (handler request)
        (catch map? {:keys [status message]}
          (log/warn status message)
          {:status status
           :body (error-body message)})
        (catch Exception e
          (log/error e)
          {:status 500
           :body (error-body (.getMessage e))}))
      (handler request))))

  
(def app (-> (make-handler route-pack handle-route)
             (wrap-exception #"/api/")
             (wrap-restful-format :formats [:transit-json])
             (wrap-defaults (-> site-defaults
                                (assoc-in [:security :anti-forgery] false)))))

(defn init!
  [])

(defn halt!
  [])
