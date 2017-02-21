(ns dev
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [figwheel-sidecar.repl-api :as figwheel]
            [test.handler :as handler]))

(defn reload-config!
  []
  (require 'test.config :reload))

(defonce server (atom nil))

(def figwheel-config
  {:figwheel-options {:css-dirs ["resources/public/css"]
                      :server-logfile "/tmp/log/figwheel.log"}
   :build-ids ["dev"]
   :all-builds
   [{:id "dev"
     :figwheel {:websocket-host "localhost"}
     :source-paths ["dev/cljs" "src/cljc" "src/cljs"]
     :compiler {:main "test.dev"
                :asset-path "/js/out"
                :output-to "resources/public/js/main.js"
                :output-dir "resources/public/js/out"}}]})

(defn start!
  []
  (when-not @server
    (reload-config!)
    (handler/init!)
    (reset! server
            (run-jetty handler/app {:port 3000
                                    :join? false})))
  (.start @server)
  (figwheel/start-figwheel! figwheel-config))

(defn stop!
  []
  (when @server
    (figwheel/stop-figwheel!)
    (.stop @server)
    (.destroy @server)
    (handler/halt!)
    (reset! server nil)))

(defn reload!
  []
  (stop!)
  (start!))

(defn cljs
  []
  (figwheel/cljs-repl "dev"))
