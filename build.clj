(ns build
  (:refer-clojure :exclude [test])
  (:require
   [clojure.java.io :as io] 
   [clojure.edn :as edn]
   [clojure.string :as str]
   [clojure.java.shell :as sh]
   [org.corfield.build :as bb]
   #_[rhinocratic.fud.config :as conf]))

(defn load-edn
  "Load EDN from a filename or io/resource"
  [source]
  (with-open [r (io/reader source)]
    (edn/read (java.io.PushbackReader. r))))

(def lib 'net.clojars.rhinocratic/fud)
(def version "0.1.0")
(def jar-version (str version "-SNAPSHOT"))
(def main 'rhinocratic.fud)
(def secrets (-> (System/getProperty "user.home")
                 (io/file ".my.secrets.edn")
                 load-edn))
;; (def config (conf/system-config :prod))

(defn test "Run the tests." 
  [opts]
  (bb/run-tests opts))

(defn ci "Run the CI pipeline of tests (and build the uberjar)."
  [opts]
  (-> opts
      (assoc :lib lib :version jar-version :main main)
      (bb/run-tests)
      (bb/clean)
      (bb/uber)))

(defn docker
  "Containerise the DB"
  [{:keys [profile] :or {profile :prod} :as _opts}]
  (let [secret (->> profile
                    name
                    (str "fud-db-password-")
                    keyword
                    secrets)]
    (println (sh/with-sh-dir "/home/vlad/Documents/dev/clj/websites/inventory/containers/db"
               (sh/with-sh-env {"POSTGRES_PASSWORD" secret}
                 (sh/sh "docker" "buildx" "build" "--secret" "id=POSTGRES_PASSWORD" "."))))))
