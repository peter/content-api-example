(defproject content-api-example "0.1.0-SNAPSHOT"
  :description "Example app for Clojure CMS REST API based on MongoDB"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [content-api "0.1.0-SNAPSHOT"]
                ]
  :min-lein-version "2.0.0"
  :uberjar-name "content-api-standalone.jar"
  :main ^:skip-aot app.content-api-example
  :target-path "target/%s"
  :profiles {
    :uberjar {:aot :all}
    :dev {:dependencies [[midje "1.6.3"]]}})
