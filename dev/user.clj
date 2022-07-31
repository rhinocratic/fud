(ns user
  (:require [integrant.repl :as ig-repl]
            [integrant.repl.state :as ig-state]
            [rhinocratic.fud.config :as config]))

(defn environment-prep!
  "Build a system configuration map, applying the given profile values"
  [profile]
  (ig-repl/set-prep! #(-> (config/system-config "config.edn" profile))))

;;;; REPL convenience functions

(defn go
  "Prepare configuration and start the system services with Integrant-repl"
  ([] (go :dev))
  ([profile] 
   (environment-prep! profile) (ig-repl/go)))

(defn reset
  "Read updates from the configuration and restart the system services with Integrant-repl"
  ([] (reset :dev))
  ([profile] 
   (environment-prep! profile) (ig-repl/reset)))

(defn halt
  "Shut down all services"
  [] (ig-repl/halt))

(defn system
  "The initialised integrant system configuration"
  [] ig-state/system)

(defn config
  "The static (uninitialised) integrant system configuration"
  [] ig-state/config)