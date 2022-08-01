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

   [rhinocratic.fud.web.api.routes :as api-routes]
   ))

(defn ping-handler [_]
  {:status 200 :body "OK"})

(defn routes [db]
  [["/api" (api-routes/routes db)]
   ["/*" (ring/create-resource-handler)]
   ["/healthz" {:get ping-handler}]])

(defmulti router
  "Create a router - a slow one for development, fast for everything else"
  (fn [key _db] key))

;; Slow router that recalculates the route tree on every invocation.
;; Useful for development whilst the tree is still changing regularly.
(defmethod router :dev
  [_ db]
  (println "Creating dev router")
  #(ring/router (routes db) {:conflicts nil}))

;; Fast router that returns the pre-calculated route tree.
(defmethod router :default
  [_ db]
  (println "Creating prod router")
  (constantly (ring/router (routes db) {:conflicts nil})))

(defn app
  "Application ring handler"
  [profile db]
  (let [r (router profile db)]
    (ring/ring-handler
     (r)
     (ring/create-default-handler))))
