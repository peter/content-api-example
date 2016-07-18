(ns app.content-api-example.models.sections
  (:require [app.content-api-example.models.shared :refer [set-sites-callback sites-schema]]
            [content-api.model-attributes :refer [translated-attribute]]
            [content-api.model-spec :refer [generate-spec]]
            [content-api.model-includes.content-base-model :refer [content-base-spec]]))

(def model-type :sections)

(defn spec [config]
  (let [locales (:locales config)]
    (generate-spec
      (content-base-spec model-type)
      {
      :type model-type
      :schema {
        :type "object"
        :properties {
          :title (translated-attribute locales)
          :description (translated-attribute locales)
          :slug (translated-attribute locales)
          :sites (sites-schema config)
          :public {:type "boolean"}
          :pages_ids {
            :type "array"
            :items {
              :type "integer"
            }
          }
          :legacy {:type "object"}
        }
        :additionalProperties false
        :required [:title :slug]
      }
      :callbacks {
        :save {
          :before [set-sites-callback]
        }
      }
      :relationships {
        :pages {}
      }
    })))
