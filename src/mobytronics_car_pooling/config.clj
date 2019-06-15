(ns mobytronics-car-pooling.config
  (:require
    [clojure.java.io :as io]
    [mount.core :as mount :refer [defstate]]
    [aero.core :refer [read-config]]))


(read-config (io/resource "config.edn"))  ;;default config

(defstate config :start  (merge (read-config
                                  (io/resource "config.edn"))  ;;default config
                                (read-config  "config/config.edn"))) ;;provided


