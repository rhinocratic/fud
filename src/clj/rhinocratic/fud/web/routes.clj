(ns rhinocratic.fud.web.routes
  (:require
   [compojure.core :refer [routes GET]]
   [compojure.route :as route]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults api-defaults]]
   [ring.util.response :refer [resource-response content-type]]))

(defn site-routes
  [opts]
  (routes
   (GET "/" [] (some-> (resource-response "index.html" {:root "public"})
                       (content-type "text/html; charset=utf-8")))
   (route/not-found "<h1>Page not found</h1>")))

(defn app-routes
  "Wrap the site and API routes with defaults"
  [opts]
  (-> (site-routes opts)
      (wrap-defaults site-defaults)))