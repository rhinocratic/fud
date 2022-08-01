(ns rhinocratic.fud
  (:gen-class)
  (:require
   [com.brunobonacci.mulog :as u]
   [integrant.core :as ig]
   [rhinocratic.fud.config :as conf]
   [rhinocratic.fud.logging :as log]
   [rhinocratic.fud.db.connection :as db]
   [rhinocratic.fud.web.router :as router]
   [rhinocratic.fud.web.server :as srv]))

;; Start logging 
(defmethod ig/init-key ::logger
  [_ config]
  (let [publishers (log/start-logging! config)]
    (u/log ::logger-started :message "Started logging")
    publishers))

;; Stop logging
(defmethod ig/halt-key! ::logger 
  [_ publishers]
  (u/log ::logger-stopping :message "Stopping logging")
  (log/stop-logging! publishers))

;; Get a database connection pool
(defmethod ig/init-key ::db
  [_ {:keys [datasource] :as _config}]
  (u/log ::open-database-pool :message "Connecting to database")
  (db/pooled-datasource datasource))

;; Close the database pool
(defmethod ig/halt-key! ::db
  [_ db]
  (u/log ::close-database-pool :message "Disconnecting from database")
  (.close db))

;; Create the application routes
(defmethod ig/init-key ::app
  [_ {:keys [profile db]}]
  (u/log ::create-app :message "Creating application routes")
  (router/app profile db))

;; Start the server
(defmethod ig/init-key ::app-server 
  [_ config]
  (u/log :message ::start-app-server "Starting app server")
  (srv/start config))

;; Stop the server 
(defmethod ig/halt-key! ::app-server 
  [_ server]
  (u/log ::stop-app-server :message "Stopping app server")
  (.stop server))

(defn stop
  "Shut down the integrant system"
  [system]
  (u/log ::shutdown :message "Shutting down")
  (ig/halt! system))

(defn start!
  "Start the integrant system, halting if any exceptions occur"
  [config]
  (try
    (ig/init config)
    (catch clojure.lang.ExceptionInfo e
      (when-let [system (:system (ex-data e))]
        (stop system))
      (throw e))))

(defn -main
  "Start the application"
  [{:keys [cmd-profile]}]
  (let [profile (or (keyword cmd-profile) :dev)
        system (-> "config.edn"
                   (conf/system-config profile)
                   start!)]
    (u/log ::startup :message (format "Starting application with profile %s" profile))
    (.addShutdownHook (Runtime/getRuntime) (Thread. ^Runnable #(stop system)))))