(ns mobytronics-car-pooling.api-requests
  (:require
    [mobytronics-car-pooling.config :refer [config]]
    [clj-http.client :as client]
    [jsonista.core :as json]
    [clojure.core.async :as async :refer [<! >! <!! timeout chan alt! go]]))

(def json-mapper
   (json/object-mapper
    {:decode-key-fn keyword}))

(def endpoint-drivers "http://apis.is/rides/samferda-drivers/")

(def endpoint-passengers "http://apis.is/rides/samferda-passengers/")




(comment  (json/read-value (:body (client/get endpoint-drivers)) json-mapper))


#_(client/get "http://www.samferda.net/en/detail/103997")


