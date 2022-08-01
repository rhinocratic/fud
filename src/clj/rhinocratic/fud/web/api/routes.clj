(ns rhinocratic.fud.web.api.routes
  (:require
   [rhinocratic.fud.web.api.handlers :as h]))

(defn routes
  []
  ["" {:get h/root-handler}])