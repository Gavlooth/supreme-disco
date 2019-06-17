(ns mobytronics-car-pooling.routes
  (:require
    [jsonista.core :as json]
    [taoensso.timbre :as timbre]
    [clojure.core.async :as async]
    [ mobytronics-car-pooling.api-requests :as api-requests]))


(def endpoint-drivers "http://apis.is/rides/samferda-drivers/")

(def endpoint-passengers "http://apis.is/rides/samferda-passengers/")



(def json-mapper
   (json/object-mapper
     {:encode-key-fn name}))



#_(defn handler [{{{:keys [x y]} :query} :parameters}]
   {:status 200
    :body {:total (+ x y)}})



(defn make-handler [endpoint]
 (fn [ {{:strs [page] :or {page "0"} } :query-params  }    respond raise]
    (let [input-Ch (async/chan 8)
          output-Ch (async/chan 8)
          response-Ch (async/chan)]
      (async/take! response-Ch #(respond {:status 200, :headers {}, :body %}))
      (api-requests/async-get-data input-Ch output-Ch endpoint page)
      (async/go
        (let  [response (async/<! (async/into [] output-Ch))]
          (async/>! response-Ch  (json/write-value-as-string response json-mapper))
          (async/close! response-Ch))))))


(def service-routes
 ["/services"
  ["/passengers"
   {:get {:handler  (make-handler endpoint-passengers)}}]
  ["/drivers"
   { :get {:handler  (make-handler endpoint-drivers)}}]])
