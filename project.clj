(defproject mobytronics-car-pooling "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [aero "1.1.3"]
                 [ring/ring-jetty-adapter "1.7.1"]
                 [metosin/reitit "0.2.13"]
                 [mount "0.1.16"]
                 [com.taoensso/timbre "4.10.0"]
                 [ring/ring-jetty-adapter "1.7.1"]
                 [clj-http "2.3.0"]
                 [metosin/jsonista "0.2.2"]
                 [clj-http "3.9.1"]
                 [org.clojure/core.async "0.4.490"]]
;  :main ^:skip-aot mobytronics-car-pooling.core
  :target-path "target/%s"
  :repl-options {:port 10001}
  :profiles {:uberjar {:aot :all}})
