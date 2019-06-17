(ns mobytronics-car-pooling.server
  (:require
    [mobytronics-car-pooling.config :refer [config]]
    [clj-http.client :as client]
    [clojure.instant :as instant]
    [jsonista.core :as json]
    [ring.adapter.jetty :as jetty]
    [ring.middleware.params :as params]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [muuntaja.core :as m]
    [mount.core :refer [defstate]]
    [reitit.ring.coercion :as coercion]
    [reitit.ring :as ring]
    [taoensso.timbre :as timbre]
    [mobytronics-car-pooling.routes :as routes])
  (:import java.text.DecimalFormat))





(def app
  (ring/ring-handler
    (ring/router
      [routes/service-routes]
      {:data {:muuntaja m/instance
              :middleware [params/wrap-params
                           muuntaja/format-middleware
                           coercion/coerce-exceptions-middleware
                           coercion/coerce-request-middleware
                           coercion/coerce-response-middleware]}})
    (ring/create-default-handler)))



(defn start-server [opts]
  (try
   (timbre/info (str "Starting Mobytronics pooling service"))
   (let [the-server (jetty/run-jetty #'app  opts)]
    (timbre/info (str "Pooling api service listening on port " (:port opts)))
    the-server)
   (catch Exception e (timbre/info "An error occured on Mobytronics pooling service  service startup " (.getMessage e)))))


(defn stop-server [server]
  (try
   (timbre/info  "Stopping server")
   (.stop server)
   (catch Exception e (timbre/info "An error occured when stopping the server " (.getMessage e)))))


(defstate server
 :start (-> config :webserver  start-server)
 :stop  (stop-server server))


