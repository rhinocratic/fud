(ns rhinocratic.fud.web.api.routes
  (:require
   [reitit.spec :as rs]
   [reitit.coercion.spec :as cs]
   [clojure.spec.alpha :as s]
   [rhinocratic.fud.web.api.handlers :as h]))

(s/def ::role #{:admin})
(s/def ::roles (s/coll-of ::role :into #{}))
(s/def ::item-id int?)

(defn handler 
  [{:keys [item-id]}]
  {:status 200 :body (format "OK - %s" item-id)})

(defn routes
  []
  [["" {:get handler}
    ["/items" {:get handler}
     ["/:item-id" {:get handler
                  #_#_:coercion cs/coercion
                  #_#_:parameters {:path {:item-id ::item-id}}}]] 
    #_{:spec (s/merge (s/keys :req [::roles]) ::rs/default-data)}]])