(ns rhinocratic.fud.web.server
  (:require
   [ring.adapter.jetty :as jetty]))

(defn start
  [{:keys [port join routes]}]
  (jetty/run-jetty routes {:port port :join? join}))