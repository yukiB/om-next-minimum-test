(defproject om-next-minimum-test "0.1.0-SNAPSHOT"
  :description ""
  :url ""
  :license {}
  :min-lein-version "2.7.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 ;; [bidi "2.0.16"]
                 [compojure "1.5.1"]
                 [hiccup "1.0.5"]
                 [ring/ring-defaults "0.2.1"]
                 [log4j/log4j "1.2.17" :exclusions [javax.mail/mail
                                                    javax.jms/jms
                                                    com.sun.jmdk/jmxtools
                                                    com.sun.jmx/jmxri]]
                 [ring/ring-jetty-adapter "1.5.0"]
                 [javax.servlet/servlet-api "2.5"]
                 [hiccup "1.0.5"]
                 [cheshire "5.6.3"]
                 [org.clojure/tools.namespace "0.2.7"]]
  :source-paths ["src/cljc" "src/clj"]
  :resource-paths ["resources"]
  :plugins [[lein-ring "0.9.7"]
            [lein-cljsbuild "1.1.5" :exclusions [org.clojure/clojure]]
            [deraen/lein-sass4clj "0.3.0"]]
  :ring {:handler handler/app
         :init handler/init!
         :war-exclusions [#".+?\.config\.clj"
                          #"log4j\.properties"]}
  :profiles {:dev {:source-paths ["dev/clj"]
                   :dependencies [;; Figwheel
                                  [figwheel-sidecar "0.5.8"]
                                  [com.cemerick/piggieback "0.2.1"]
                                  [com.cognitect/transit-clj "0.8.297"]

                                  ;; ClojureScript
                                  [org.clojure/clojurescript "1.9.293"]
                                  [org.clojure/core.async "0.2.395"]
                                  [org.omcljs/om "1.0.0-alpha30"]
                                  [prismatic/om-tools "0.4.0"]
                                  [cljsjs/react "15.4.0-0"]
                                  [cljsjs/react-dom "15.4.0-0"]
                                  [sablono "0.7.6"]
                                  [cljs-http "0.1.42"]
                                  [kibu/pushy "0.3.6"]]}
             :uberjar {:aot :all}
             :main {:dependencies [[javax.servlet/servlet-api "2.5"]
                                   [ring/ring-jetty-adapter "1.5.0"]]
                    :main test.boot}}
  :cljsbuild {:builds
              [{:source-paths ["src/cljc" "src/cljs"]
                :compiler {:output-to "resources/public/js/main.js"
                           :optimizations :advanced
                           :pretty-print false}}]}
  :clean-targets ^{:protect false} [:target-path
                                    :compile-path
                                    "resources/public/css"
                                    "resources/public/js/main.js"
                                    "resources/public/js/main.js.map"
                                    "resources/public/js/dev"]
  :jar-exclusions [#".+?\.config\.clj"
                   #"log4j\.properties"]
  :sass {:source-paths ["src/scss"]
         :target-path "resources/public/css"
         :output-style :compressed})
