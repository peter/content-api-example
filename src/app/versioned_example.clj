(ns app.versioned-example
  (:require [versioned :as versioned]))

(def sites ["se" "no" "dk" "fi"])

(def default-config {
  :models {
    :sections "app.versioned-example.models.sections/spec"
    :pages "app.versioned-example.models.pages/spec"
    :widgets "app.versioned-example.models.widgets/spec"
  }
  :sites sites
  :locales sites
  :mongodb-url "mongodb://127.0.0.1/versioned-example"
})

(defn -main [& {:as custom-config}]
  (let [config (merge default-config custom-config)
        args-list (apply concat (seq config))]
    (apply versioned/-main args-list)))
