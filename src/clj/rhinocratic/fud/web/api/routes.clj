(ns rhinocratic.fud.web.api.routes
  (:require
   [rhinocratic.fud.web.api.handlers :as h]))

(defn wrap-db
  [handler db]
  (fn [request] 
    (handler (assoc request :db db))))

(defn routes
  [db]
  [["" {:get h/root-handler
        :middleware [[wrap-db db]]}
    ["/suppliers" {:get h/suppliers-handler-get}]
    ["/items" {:get h/fud-items-handler-get}]]])