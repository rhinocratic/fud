(ns rhinocratic.fud.web.site.handlers
  (:require
   [ring.util.response :refer [resource-response content-type]]))

(defn home-page-handler [_]
  (some-> (resource-response "index.html" {:root "public"})
          (content-type "text/html; charset=utf-8")))