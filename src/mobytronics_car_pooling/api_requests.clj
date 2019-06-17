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


(defn exctract-info [response]
 (let [xsub-form (comp (remove #{"\n"})
                       (map :content))
       xform (comp (map :content)
                   (map (partial sequence xsub-form))
                   (map (fn [[ [ { [x] :content}] [y]]]
                          (let [x' (apply str (remove #{\:} (seq x)))]
                           {x' y}))))]
   (-> response :body parse as-hickory (->> (s/select  (s/tag :tr)) (transduce xform merge)))))

(defn  add-distance-info [{:strs [From To] :as info}]
 (let [url-encoded (URLEncoder/encode  (str From"|"To) "UTF-8")]
  (-> 
      (str "https://www.distance24.org/route.json?stops=" url-encoded)
      client/get
      :body
      json/read-value
      (get "distance")
      (->> (assoc info "Distance")))))

(defn async-get-info [link channel]
  (let [intermediate-channel (async/chan)]
    (async/go
      (async/>! intermediate-channel  (exctract-info (client/get link))))
    (async/go
      (let [info (async/<! intermediate-channel)]
       (async/>! channel (add-distance-info info))
       (async/close! channel)))))


(defn async-get-data [input-Ch output-Ch endpoint page]
  (let [the-page  (Integer/parseInt  page)
        links (take 8 (drop (* 8 (dec the-page))
                            (map :link (:results
                                         (json/read-value
                                           (:body (client/get endpoint))
                                           json-mapper)))))]
    (async/onto-chan input-Ch links)
    (async/pipeline-async 8 output-Ch async-get-info input-Ch)))




