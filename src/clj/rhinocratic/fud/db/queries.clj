(ns rhinocratic.fud.db.queries
  (:require 
   [next.jdbc :as jdbc]
   [honey.sql :as sql]
   [honey.sql.helpers :as h]))

(defn select-all-items
  [db table cols]
  (jdbc/execute! db
                 (-> (apply h/select cols)
                     (h/from table)
                     sql/format)))

(defn select-item-by-id 
  [db table cols pkey id]
  (jdbc/execute-one! db
                     (-> (apply h/select cols)
                         (h/from table)
                         (h/where [:= pkey id])
                         sql/format)))

(defmulti select-all 
  "Select all items from a table"
  (fn [_db table] table))

(defmulti select-by-id
  "Select an item from a table by primary key"
  (fn [_db table _id] table))

(defmethod select-all :supplier
  [db table]
  (select-all-items db table [:supplier_id :supplier_name :website]))

(defmethod select-all :fud_item 
  [db table]
  (select-all-items db table [:fud_item_name :notes]))

(defmethod select-by-id :fud_item
  [db table id]
  (select-item-by-id db table [:fud_item_name :notes] :fud_item_id id))
 
(defn new-supplier
  [db {:keys [name website]}]
  (jdbc/execute-one! db 
                     (-> (h/insert-into :supplier)
                         (h/values [{:supplier_name name
                                     :website website}])
                         sql/format)))


(comment

  (let [db (:rhinocratic.fud/db (user/system))]
    (all-suppliers db))

  (let [db (:rhinocratic.fud/db (user/system))]
    (new-supplier db {:name "The Italian Shop"
                        :website "https://italianfoodshop.co.uk/"}))
  
  
  )