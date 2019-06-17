(ns mobytronics-car-pooling.api-requests
  (:require
    [mobytronics-car-pooling.config :refer [config]]
    [clj-http.client :as client]
    [jsonista.core :as json]
    [clojure.core.async :as async] ;:refer [<! >! <!! timeout chan alt! go]]
    [hickory.core :refer [as-hickory as-hiccup parse parse-fragment]]
    [hickory.select :as s]
    [taoensso.timbre :as timbre])
 (:import  java.net.URLEncoder))

(def json-mapper
   (json/object-mapper
    {:decode-key-fn keyword}))
(def endpoint-drivers "http://apis.is/rides/samferda-drivers/")


(def endpoint-passengers "http://apis.is/rides/samferda-passengers/")

(def distance24 "https://www.distance24.org/route.json?stops=Selfoss|Lugar")

(def distance24-2 "https://www.distance24.org/route.json?stops=Selfoss|Reykjavík")

(def input-Ch (async/chan 100))

(def output-Ch (async/chan 100))


(defn exctract-driver-info [response]
 (let [xsub-form (comp (remove #{"\n"})
                       (map :content))
       xform (comp (map :content)
                   (map (partial sequence xsub-form))
                   (map (fn [[ [ { [x] :content}] [y]]]
                          (let [x' (apply str (remove #{\:} (seq x)))]
                           {x' y}))))]
   (-> response :body parse as-hickory (->> (s/select  (s/tag :tr)) (transduce xform merge)))))

(defn  add-distance-info [{:strs [From To] :as driver-info}]
 (let [url-encoded (URLEncoder/encode  (str From"|"To) "UTF-8")]
  (.println System/out (str "https://www.distance24.org/route.json?stops="   url-encoded))
  (-> 
      (str "https://www.distance24.org/route.json?stops=" url-encoded)
      client/get
      :body
      json/read-value
      (get "distance")
      (->> (assoc driver-info "Distance")))))

(defn async-get-driver-info [link channel]
  (let [intermediate-channel (async/chan)]
    (async/go
      (async/>! intermediate-channel  (exctract-driver-info (client/get link))))
    (async/go
     (let [driver-info (async/<! intermediate-channel)]
      (async/>! channel (add-distance-info driver-info))
      (async/close! channel)))))


(defn get-driver-links []
  (let [links (map :link (:results (json/read-value (:body (client/get endpoint-drivers)) json-mapper)))]
   (async/onto-chan input-Ch links false)
   (async/pipeline-async 30 output-Ch async-get-driver-info input-Ch  false)))


(comment
  (def end-point-results (delay (:results (json/read-value (:body (client/get endpoint-drivers)) json-mapper)))))

(comment (def tmp (atom [])))

(comment (async/go-loop []
                (when-let [a (async/<! output-Ch)]
                  (swap! tmp conj a)
                  #_(.println System/out (str "going loop "  a))
                 (recur))))


(comment (do (get-driver-links)))
(comment (map #(get % "Distance")(deref tmp)))





