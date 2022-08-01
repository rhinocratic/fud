(ns rhinocratic.fud.web.api.admin.routes
  (:require 
   [rhinocratic.fud.web.api.admin.handlers :as h]))

(defn routes
  []
  ["" {:get h/root-handler}])