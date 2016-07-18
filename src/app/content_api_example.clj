(ns app.content-api-example
  (:require [content-api :as content-api]))

(def sites ["se" "no" "dk" "fi"])

(def default-config {
  :models {
    :sections "app.content-api-example.models.sections/spec"
    :pages "app.content-api-example.models.pages/spec"
    :widgets "app.content-api-example.models.widgets/spec"
  }
  :sites sites
  :locales sites
})

(defn -main [& {:as custom-config}]
  (let [config (merge default-config custom-config)
        args-list (apply concat (seq config))]
    (apply content-api/-main args-list)))
