(ns rhinocratic.fud.db.connection
  (:require
   [next.jdbc :as jdbc]
   [next.jdbc.connection :as connection])
  (:import (com.zaxxer.hikari HikariDataSource)))

(defn pooled-datasource
  "Create a JDBC DataSource from a map containing a DB spec."
  ^HikariDataSource [datasource]
  (let [datasource (connection/->pool com.zaxxer.hikari.HikariDataSource datasource)]
    (.close (jdbc/get-connection datasource)) ;; Get a connection and immediately close it, just to instantiate the data source.
    datasource))
