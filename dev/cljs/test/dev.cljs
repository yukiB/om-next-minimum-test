(ns test.dev
  (:require [test.core :as core]))

(enable-console-print!)

;;; This is hack for rerun entry-point on reload by figwheel
(defonce ^:dynamic reloaded? nil)

(when reloaded?
  (core/init!))


(set! reloaded? true)
