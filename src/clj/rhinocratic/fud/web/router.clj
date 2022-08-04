(ns rhinocratic.fud.web.router
  (:require
   [reitit.core :as r]
   [reitit.ring :as ring]
   [reitit.coercion.spec :as coercion-spec]
   [reitit.ring.coercion :as ring-coercion]
   [reitit.ring.spec :as rrs]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.multipart :as multipart]
   [reitit.ring.middleware.parameters :as parameters]
   [reitit.spec :as rs]
   [muuntaja.core :as m]
   [expound.alpha :as e]
   [reitit.swagger :as swagger]
   [reitit.swagger-ui :as swagger-ui]
   [rhinocratic.fud.web.api.routes :as api-routes]))

(defn ping-handler [_]
  {:status 200 :body "OK"})

(defn routes
  []
  [["/api" (api-routes/routes)]
   ["/healthz" {:get {:handler ping-handler :no-doc true}}]
   ["/swagger.json"
    {:get {:no-doc true
           :swagger {:info {:title "my-api"}}
           :handler (swagger/create-swagger-handler)}}]])

(def router-opts
 "Router options - common to dev and prod routers" 
  {:validate rrs/validate
   ::rs/explain e/expound-str
   :data {:muuntaja m/instance
          :coercion coercion-spec/coercion
          :middleware [muuntaja/format-middleware
                       parameters/parameters-middleware
                       multipart/multipart-middleware
                       ring-coercion/coerce-exceptions-middleware
                       ring-coercion/coerce-request-middleware
                       ring-coercion/coerce-response-middleware]}})

(defmulti router
  "Create a router - a slow one for development, fast for everything else"
  (fn [key _db] key))

;; Slow router that recalculates the route tree on every invocation.
;; Useful for development whilst the tree is still changing regularly.
(defmethod router :dev
  [_ db]
  (println "Creating dev router")
  #(ring/router (routes) router-opts))

;; Fast router that returns the pre-calculated route tree.
(defmethod router :default
  [_ db]
  (println "Creating prod router")
  (constantly (ring/router (routes) router-opts)))

(defn app
  "Application ring handler"
  [profile db]
  (let [r (router profile db)]
    (ring/ring-handler
     (r)
     (ring/routes
      (swagger-ui/create-swagger-ui-handler {:path "/api-docs"})
      (ring/create-resource-handler {:path "/"})
      (ring/create-default-handler)))))


(comment

  (def router
    (r/router
     [["/ping" ::ping]
      ["/pong" identity]
      ["/users" {:get {:roles #{:admin}
                       :handler identity}}]]))

  (r/routes router)
  (r/match-by-path router "/ping")
  (r/match-by-name router ::ping)
  
  #_{})