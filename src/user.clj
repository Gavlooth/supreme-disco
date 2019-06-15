(ns user
 (:require [clojure.tools.namespace.repl :as tn]
           [mount.core :as mount]
           [mobytronics-car-pooling.server :refer [server]]
           [mobytronics-car-pooling.config :refer [config]]))

(defn go []
 (set! *warn-on-reflection* true)
 (mount/start #'config  #'server))


(defn refresh-all []
 (mount/stop)
 (tn/refresh-all))


(defn reset []
 (mount/stop)
 (tn/refresh)
 (go))
