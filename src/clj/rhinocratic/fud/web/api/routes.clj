(ns rhinocratic.fud.web.api.routes
  (:require
   [reitit.spec :as rs]
   [reitit.coercion.spec :as cs]
   [clojure.spec.alpha :as s]
   [rhinocratic.fud.web.api.handlers :as h]))

(s/def ::role #{:admin})

(defn handler 
  [args]
  {:status 200 :body (format "OK - %s" (get-in args [:path-params :item-id]))})

(defn routes
  []
  [["" {:get handler}]
   ["/items" {:get handler}]
   ["/items/:item-id" {:get handler
                      :coercion cs/coercion
                      :parameters {:path {:item-id int?}}}]]
   #_{:spec (s/merge (s/keys :req [::roles]) ::rs/default-data)})