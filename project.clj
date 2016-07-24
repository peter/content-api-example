(defproject versioned-example "0.1.0-SNAPSHOT"
  :description "Example app for Clojure CMS REST API based on MongoDB"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [versioned "0.1.0-SNAPSHOT"]
                ]
  :min-lein-version "2.0.0"
  :uberjar-name "versioned-standalone.jar"
  :main ^:skip-aot app.versioned-example
  :target-path "target/%s"
  :profiles {
    :uberjar {:aot :all}
    :dev {:dependencies [[midje "1.6.3"]]}})
