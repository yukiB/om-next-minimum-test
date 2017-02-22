(defproject om-next-minimum-test "0.1.0-SNAPSHOT"
  :description ""
  :url ""
  :license {}
  :min-lein-version "2.7.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [bidi "2.0.16"]
                 [compojure "1.5.1"]
                 [hiccup "1.0.5"]
                 [ring/ring-defaults "0.2.1"]
                 [org.apache.logging.log4j/log4j-core "2.8"]
                 [org.apache.logging.log4j/log4j-slf4j-impl "2.8"]
                 [ring/ring-jetty-adapter "1.5.0"]
                 [ring-middleware-format "0.7.2"]
                 [javax.servlet/servlet-api "2.5"]
                 [buddy/buddy-auth "0.6.1"] ;; NB: Do not upgrade to 0.6.1 or above, enbugged with https://github.com/funcool/buddy-auth/pull/29
                 [buddy/buddy-hashers "0.6.0"]
                 [hiccup "1.0.5"]
                 [cheshire "5.6.3"]
                 [ring/ring-core "1.5.1" :exclusions [org.clojure/tools.reader]]
                 [slingshot "0.12.2"]
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
                                  [com.cognitect/transit-cljs "0.8.239"]
                                  [org.omcljs/om "1.0.0-alpha47"]
                                  [prismatic/om-tools "0.4.0"]
                                  [cljsjs/react "15.4.0-0"]
                                  [cljsjs/react-dom "15.4.0-0"]
                                  [cljs-ajax "0.5.8"]
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
