(ns rhinocratic.fud.web.site.routes
  (:require
   [rhinocratic.fud.web.site.handlers :as h]))

(defn routes
  []
  ["" {:get h/home-page-handler}])