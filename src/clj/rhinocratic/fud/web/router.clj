(ns rhinocratic.fud.web.router
  (:require
   [ring.middleware.defaults :refer [wrap-defaults site-defaults api-defaults]]

   ;; Core Web Application Libraries
   [muuntaja.core :as muuntaja]
   [reitit.ring :as ring]
   [reitit.ring.middleware.muuntaja :as middleware-muuntaja]
   [reitit.ring.middleware.parameters :as parameters]
   [reitit.ring.middleware.exception :as exception]

   ;; Self-documenting API
   [reitit.swagger :as api-docs]
   [reitit.swagger-ui :as api-docs-ui]

   ;; Provide details of parameters to API documentation UI (swagger)
   [reitit.coercion.spec]
   [reitit.ring.coercion :as coercion]

   [rhinocratic.fud.web.site.routes :as site-public-routes]
   [rhinocratic.fud.web.site.admin.routes :as site-admin-routes]
   [rhinocratic.fud.web.api.routes :as api-public-routes]
   [rhinocratic.fud.web.api.admin.routes :as api-admin-routes]
   ))

(defn ping-handler [_]
  {:status 200 :body "OK"})

(defn routes []
  [["/"
    (site-public-routes/routes)]
   ["/admin"
    (site-admin-routes/routes)]
   ["/api"
    (api-public-routes/routes)]
   ["/api/admin"
    (api-admin-routes/routes)]
   ["/healthz" {:get ping-handler}]])

(defmulti router 
  "Create a router - a slow one for development, fast for everything else"
  identity)

;; Slow router that recalculates the route tree on every invocation.
;; Useful for development whilst the tree is still changing regularly.
(defmethod router :dev
  [_]
  (println "Creating dev router")
  #(ring/router (routes)))

;; Fast router that returns the pre-calculated route tree.
(defmethod router :default
  [_]
  (println "Creating prod router")
  (constantly (ring/router (routes))))

(defn app
  "Application ring handler"
  [profile]
  (let [r (router profile)]
    (ring/ring-handler (r))))
