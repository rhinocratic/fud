(ns rhinocratic.fud.config
  (:require
   [aero.core :as aero]
   [clojure.java.io :as io]
   [integrant.core :as ig]))

;; Parse Integrant key with with aero tag literals, returning key with profile value
(defmethod aero/reader 'ig/ref
  [_ _tag value]
  (ig/ref value))

;; Coerce a config value to int32
(defmethod aero/reader 'int32
  [_ _ value]
  (if (number? value)
    (int value)
    (Integer/parseInt value)))

;; Insert a value from the aero options into the configuration
(defmethod aero/reader 'opt
  [opts _ value]
  (if (keyword? value)
    (opts value)
    (opts (keyword value))))

(defn integrant-entry?
  "A configuration entry is assumed to be an integrant entry if it has a qualified key, 
   or if it has a composite key consisting of a vector of qualified keys."
  [[k _]]
  (or (qualified-keyword? k)
      (ig/valid-config-key? k)))

(defn remove-aero-entries
  "Removes Aero keys from the configuration map (Aero splices the values of these 
   keys into the structures that refer to them, but leaves their entries in the 
   configuration map.  Integrant will complain about missing init-key multimethods 
   for these keys unless they are removed)."
  [config]
  (->> config
       (filter integrant-entry?)
       (into {})))

(defn system-config
  "Read the given configuration file and return a configuration map, substituting 
   Aero references as appropriate for the given profile.
   Removes non-integrant entries from the resulting configuration.
   Loads any namespaces referred to by integrant keys in the configuration.
   Performs any prepping of integrant keys."
  [config-file-name profile]
  (-> config-file-name
      io/resource
      (aero/read-config {:profile profile})
      remove-aero-entries
      ((fn [config] (ig/load-namespaces config) config))
      ig/prep))