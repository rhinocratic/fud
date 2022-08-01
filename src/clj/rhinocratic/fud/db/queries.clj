(ns rhinocratic.fud.db.queries
  (:require 
   [next.jdbc :as jdbc]
   [honey.sql :as sql]
   [honey.sql.helpers :as h]))

(defn all-suppliers
  [conn]
  (jdbc/execute! conn
                 (-> (h/select :supplier_id :supplier_name :website)
                     (h/from :supplier)
                     sql/format)))

(defn all-fud-items
  [conn]
  (jdbc/execute! conn
                 (-> (h/select :fud_item_name :notes)
                     (h/from :fud_item)
                     sql/format)))

(defn new-supplier
  [conn {:keys [name website]}]
  (jdbc/execute-one! conn 
                     (-> (h/insert-into :supplier)
                         (h/values [{:supplier_name name
                                     :website website}])
                         sql/format)))


(comment 
  
  (let [conn (:rhinocratic.fud/db (user/system))]
    (all-suppliers conn))
  
  (let [conn (:rhinocratic.fud/db (user/system))]
    (new-supplier conn {:name "The Italian Shop"
                        :website "https://italianfoodshop.co.uk/"}))
  
  )