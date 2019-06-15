(ns mobytronics-car-pooling.routes
  (:require
    [jsonista.core :as json]
    [taoensso.timbre :as timbre]))


(def json-mapper
   (json/object-mapper
    {:decode-key-fn str}))

;[data image image-type image-dimensions]

(defn transform-fn [request]
       {:status 200
        :body  "test"})



(defn handler [{{{:keys [whatever]} :query} :parameters}]
 {:status 200
  :body {:test "tmp-string"}})

(def service-routes
 ["/services"
  ["mobytronics"
   {:post  transform-fn 
    :get {:handler  handler}}]])




