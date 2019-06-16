(ns mobytronics-car-pooling.api-requests
  (:require
    [mobytronics-car-pooling.config :refer [config]]
    [clj-http.client :as client]
    [jsonista.core :as json]
    [clojure.core.async :as async] ;:refer [<! >! <!! timeout chan alt! go]]
    [hickory.core :refer [as-hickory as-hiccup parse parse-fragment]]
    [hickory.select :as s]
    [taoensso.timbre :as timbre]))



(def json-mapper
   (json/object-mapper
    {:decode-key-fn keyword}))
(def endpoint-drivers "http://apis.is/rides/samferda-drivers/")


(def endpoint-passengers "http://apis.is/rides/samferda-passengers/")

(def distance24 "https://www.distance24.org/route.json?stops=Selfoss|Lugar")

(def distance24-2 "https://www.distance24.org/route.json?stops=Selfoss|Reykjavík")

(deref sample-distance)

(def input-Ch (async/chan 100))

(def output-Ch (async/chan 100))


(defn exctract-driver-info [response]
 (let [xsub-form (comp (remove #{"\n"}) (map :content))
       xform (comp (map :content) (map (partial sequence xsub-form)) (map (fn [[ [ { [x] :content}] [y]]] {x y})))]
   (-> response :body parse as-hickory (->> (s/select  (s/tag :tr)) (sequence xform)))))

(def tmp-1 (atom []))

(defn async-get-driver-info [link channel]
  (async/go
    (async/>! channel (exctract-driver-info (client/get link)))
    (async/close! channel)))

(defn get-driver-links []
  (let [links (map :link (:results (json/read-value (:body (client/get endpoint-drivers)) json-mapper)))]
   (async/onto-chan input-Ch links false)
   (async/pipeline-async 15 output-Ch async-get-driver-info input-Ch  false)))


(def end-point-results (delay (:results (json/read-value (:body (client/get endpoint-drivers)) json-mapper))))

(def tmp (atom []))

(async/go-loop []
         (when-let [a (async/<! output-Ch)]
           (.println System/out (str "going loop " (into [] a))) 
           (swap! tmp conj a)
          (recur)))

(comment (do (get-driver-links))) 
(comment (first (deref tmp)))






