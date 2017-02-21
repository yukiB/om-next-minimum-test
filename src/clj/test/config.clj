(ns test.config
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]))

(def ^:private default-config-filename "config.edn")

(defn- load-config!
  [f]
  (try (-> (io/resource f)
           slurp
           edn/read-string)
       (catch Exception e
         (println f "configuration file is not found")
         nil)))

(def config (load-config! default-config-filename))
