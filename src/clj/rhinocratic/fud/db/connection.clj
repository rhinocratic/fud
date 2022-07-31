(ns rhinocratic.fud.db.connection
  (:require
   [next.jdbc :as jdbc]
   [next.jdbc.connection :as connection])
  (:import (com.zaxxer.hikari HikariDataSource)))

(defn ^HikariDataSource pooled-datasource
  "Create a JDBC DataSource from a map containing a DB spec."
  [datasource]
  (let [datasource (connection/->pool com.zaxxer.hikari.HikariDataSource datasource)]
    (.close (jdbc/get-connection datasource))
    datasource))

