(ns rhinocratic.fud.web.site.admin.routes
  (:require
   [rhinocratic.fud.web.site.admin.handlers :as h]))

(defn routes
  []
  ["" {:get h/root-handler}])