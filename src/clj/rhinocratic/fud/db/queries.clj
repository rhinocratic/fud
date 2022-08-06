(ns rhinocratic.fud.db.queries
  (:require 
   [next.jdbc :as jdbc]
   [honey.sql :as sql]
   [honey.sql.helpers :as h]
   [com.brunobonacci.mulog :as u]))

(defn- primary-key
  "Derive the primary key from the table name"
  [table]
  (-> table
      name
      (str "_id")
      keyword))

(def ^:private display-columns
  "Map containing the columns that should be returned by a SELECT for each table"
  {:brand [:brand_id :brand_name :notes]
   :supplier [:supplier_id :supplier_name :website :email :telephone :address :notes]
   :fud_category [:fud_category_id :fud_category_name :notes]
   :fud_item [:fud_item_id :fud_item_name :low_stock_warning_level :notes :brand_id :supplier_id]
   :inventory_item [:inventory_item_id :fud_item_id :expiry_day :expiry_month :expiry_year :date_expiry :date_added :date_used]})

(defn- select-all-sql 
  [table cols]
  (-> (apply h/select cols)
      (h/from table)
      sql/format))

(defn- select-all-rows
  "Select all rows from a table"
  [db table cols]
  (try
    (jdbc/execute! db (select-all-sql table cols))
    (catch Exception e 
      (u/log ::select-all-rows-error :message (ex-message e))
      (throw (ex-info "Error whilst selecting rows" {:table table :cols cols})))))

(defn- select-by-id-sql 
  [table id cols]
  (-> (apply h/select cols)
      (h/from table)
      (h/where [:= (primary-key table) id])
      sql/format))

(defn- select-row-by-id 
  "Select the row with the given ID from a table"
  [db table id cols]
  (try
    (jdbc/execute-one! db (select-by-id-sql table id cols))
    (catch Exception e
      (u/log ::select-row-by-id-error :message (ex-message e))
      (throw (ex-info "Eror whilst selecting row" {:table table :cols cols :id id})))))

(defn- insert-row-sql
  [table row]
  (-> (h/insert-into table)
      (h/values [row])
      (h/returning (primary-key table))
      (sql/format {:pretty true})))

(defn- insert-row 
  "Insert a new row into a table"
  [db table row]
  (try
    (jdbc/execute-one! db (insert-row-sql table row))
    (catch Exception e 
      (u/log ::insert-row-error :message (ex-message e))
      (throw (ex-info "Error whilst inserting row" {:table table :row row})))))

(defn- delete-row-sql
  "Delete a row from a table"
  [table id]
  (-> (h/delete-from table)
      (h/where [:= (primary-key table) id])
      (h/returning (display-columns table))
      (sql/format {:pretty true})))

(defn- delete-row 
  "Delete a row from a table"
  [db table id]
  (try 
    (jdbc/execute-one! db (delete-row-sql table id))
    (catch Exception e 
      (u/log ::delete-row-error :message (ex-message e))
      (throw (ex-info "Error whilst deleting row" {})))))

(defn select-all 
  "Select all items from a table"
  [db table]
  (select-all-rows db table (display-columns table)))

(defn select-by-id
  "Select an item from a table by primary key"
  [db table id]
  (select-row-by-id db table id (display-columns table)))

(defn delete-by-id
  "Delete a row by ID"
  [db table id]
  (delete-row db table id))

(defmulti ^:private augment-new-row
  "Do any required processing of a new row before committing it to the DB"
  (fn [table _row] table))

(defmethod augment-new-row :inventory_item
  [_table row]
  (assoc row
         :date-added (java.time.LocalDateTime/now)
         :expiry_date (java.time.LocalDateTime/now)))

(defn create-new
  "Create a new row"
  [db table row]
  (insert-row db table (augment-new-row table row)))
