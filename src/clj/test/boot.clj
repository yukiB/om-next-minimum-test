(ns test.boot
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [test.handler :as handler])
  (:gen-class))

(defn -main
  [& args]
  (handler/init!)
  (run-jetty handler/app {:port (or (some-> args first Integer/parseInt)
                                    3000)
                          :join? false}))
