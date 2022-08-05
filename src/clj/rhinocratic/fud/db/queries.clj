(ns rhinocratic.fud.db.queries
  (:require 
   [next.jdbc :as jdbc]
   [honey.sql :as sql]
   [honey.sql.helpers :as h]
   [com.brunobonacci.mulog :as u]))

(defn select-all-sql 
  [table cols]
  (-> (apply h/select cols)
      (h/from table)
      sql/format))

(defn select-all-rows
  "Select all rows from a table"
  [db table cols]
  (try
    (jdbc/execute! db (select-all-sql table cols))
    (catch Exception e 
      (u/log ::select-all-rows-error :message (ex-message e))
      (throw (ex-info "SQL error whilst selecting rows" {})))))

(defn select-by-id-sql 
  [table cols pkey id]
  (-> (apply h/select cols)
      (h/from table)
      (h/where [:= pkey id])
      sql/format))

(defn select-row-by-id 
  "Select the row with the given ID from a table"
  [db table cols pkey id]
  (try
    (jdbc/execute-one! db (select-by-id-sql table cols pkey id))
    (catch Exception e
      (u/log ::select-row-by-id-error :message (ex-message e))
      (throw (ex-info "SQL error whilst selecting row" {})))))

(defn insert-row-sql
  [table row pkey]
  (-> (h/insert-into table)
      (h/values [row])
      (h/returning pkey)
      (sql/format {:pretty true})))

(defn insert-row 
  "Insert a new row into a table"
  [db table row pkey]
  (try
    (jdbc/execute-one! db (insert-row-sql table row pkey))
    (catch Exception e 
      (u/log ::insert-row-error :message (ex-message e))
      (throw (ex-info "SQL error whilst inserting row" {})))))

(defmulti select-all 
  "Select all items from a table"
  (fn [_db table] table))

(defmulti select-by-id
  "Select an item from a table by primary key"
  (fn [_db table _id] table))

(defmulti create-new 
  "Create a new row"
  (fn [_db table _row] table))

(defmethod select-all :brand
  [db table]
  (select-all-rows db table [:brand_id :brand_name :notes]))

(defmethod select-all :supplier
  [db table]
  (select-all-rows db table [:supplier_id :supplier_name :website :email :telephone :address :notes]))

(defmethod select-all :fud_category
  [db table]
  (select-all-rows db table [:fud_category_id :fud_category_name :notes]))

(defmethod select-all :fud_item 
  [db table]
  (select-all-rows db table [:fud_item_id :fud_item_name :low_stock_warning_level :notes :brand_id :supplier_id]))

(defmethod select-all :inventory_item
  [db table]
  (select-all-rows db table [:inventory_item_id :fud_item_id :expiry_day :expiry_month :expiry_year :date_expiry :date_added :date_used]))

(defmethod select-by-id :brand
  [db table id]
  (select-row-by-id db table [:brand_id :brand_name :notes] :brand_id id))

(defmethod select-by-id :supplier
  [db table id]
  (select-row-by-id db table [:supplier_id :supplier_name :website :email :telephone :address :notes] :supplier_id id))

(defmethod select-by-id :fud_category
  [db table id]
  (select-row-by-id db table [:fud_category_id :fud_category_name :notes] :fud_category_id id))

(defmethod select-by-id :fud_item
  [db table id]
  (select-row-by-id db table [:fud_item_id :fud_item_name :low_stock_warning_level :notes :brand_id :supplier_id] :fud_item_id id))
 
(defmethod select-by-id :inventory_item
  [db table id]
  (select-row-by-id db table [:inventory_item_id :fud_item_id :expiry_day :expiry_month :expiry_year :date_expiry :date_added :date_used] :inventory_item_id id))

(defmethod create-new :brand 
  [db table row]
  (insert-row db table row :brand_id))

(defmethod create-new :supplier 
  [db table row]
  (insert-row db table row :supplier_id))

(defmethod create-new :fud_category
  [db table row]
  (insert-row db table row :fud_category_id))

(defmethod create-new :fud_item
  [db table row]
  (insert-row db table row :fud_item_id))

(defmethod create-new :inventory_item
  [db table row]
  (insert-row db table (assoc row
                              :date-added (java.time.LocalDateTime/now)
                              :expiry_date (java.time.LocalDateTime/now))
              :inventory_item_id))



(comment

  (let [db (:rhinocratic.fud/db (user/system))]
    (select-all db :supplier))

  (let [db (:rhinocratic.fud/db (user/system))]
    (create-new db :supplier {:supplier_name "The Italian Shop"
                              :website "https://italianfoodshop.co.uk/"}))
  
  
  )