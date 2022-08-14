(ns rhinocratic.fud.db.queries
  (:require
   [clojure.string :as str]
   [next.jdbc :as jdbc]
   [honey.sql :as sql]
   [honey.sql.helpers :as h]
   [com.brunobonacci.mulog :as u]))

(defn primary-key
  "Derive the primary key from the table name"
  [table]
  (-> table
      name
      (str "_id")
      keyword))

(defn returning
  "Calls honey-sql 'returning' with a vector of columns"
  [builder cols]
  (apply (partial h/returning builder) cols))

(def ^:private display-columns
  "Map containing the columns that should be returned by a SELECT for each table"
  {:brand [:brand_id :brand_name :notes]
   :supplier [:supplier_id :supplier_name :website :email :telephone :address :notes]
   :fud_category [:fud_category_id :fud_category_name :notes]
   :fud_type [:fud_type_id :fud_type_name :low_stock_qty :low_stock_unit_id :notes]
   :fud_item [:fud_item_id :fud_type_id :brand_id :unit_qty :unit_id :notes]
   :inventory_item [:inventory_item_id :fud_item_id :expiry_day :expiry_month :expiry_year :date_expiry :date_added :date_used]
   :unit [:unit_id :unit_name]})

(defn- select-all-sql
  [table]
  (-> (apply h/select (display-columns table))
      (h/from table)
      (sql/format {:pretty true})))

(defn select-all
  "Select all rows from a table"
  [db table]
  (try
    (jdbc/execute! db (select-all-sql table))
    (catch Exception e
      (u/log ::select-all-rows-error :message (ex-message e) :table table)
      {:error {:status 500}})))

(defn- select-by-id-sql
  [table id]
  (-> (apply h/select (display-columns table))
      (h/from table)
      (h/where [:= (primary-key table) id])
      (sql/format {:pretty true})))

(defn select-one
  "Select the row with the given ID from a table"
  [db table id]
  (try
    (jdbc/execute-one! db (select-by-id-sql table id))
    (catch Exception e
      (u/log ::select-row-by-id-error :message (ex-message e) :table table :id id)
      {:error {:status 500}})))

(defn- insert-row-sql
  [table row]
  (-> (h/insert-into table)
      (h/values [row])
      (returning (display-columns table))
      (sql/format {:pretty true})))

(defn insert
  "Insert a new row into a table"
  [db table row]
  (try
    (jdbc/execute-one! db (insert-row-sql table row))
    (catch Exception e
      (u/log ::insert-row-error :message (ex-message e) :table table :row row)
      (if (str/includes? (ex-message e) "duplicate")
        {:error {:status 409}}
        {:error {:status 500}}))))

(defn- delete-row-sql
  [table id]
  (-> (h/delete-from table)
      (h/where [:= (primary-key table) id])
      (returning (display-columns table))
      (sql/format {:pretty true})))

(defn delete
  "Delete a row from a table"
  [db table id]
  (try
    (jdbc/execute-one! db (delete-row-sql table id))
    (catch Exception e
      (u/log ::delete-row-error :message (ex-message e) :table table :id id)
      {:error {:status 500}})))

(defn- update-row-sql
  [table id row]
  (-> (h/update table)
      (h/where [:= (primary-key table) id])
      (h/set row)
      (returning (display-columns table))
      (sql/format {:pretty true})))

(defn- update-row
  [db table id row]
  (try
    (jdbc/execute-one! db (update-row-sql table id row))
    (catch Exception e
      (u/log ::update-row-error :message (ex-message e) :table table :id id :row row)
      (if (str/includes? (ex-message e) "duplicate")
        {:error {:status 409}}
        {:error {:status 500}}))))

(defmulti ^:private augment-row
  "Do any required processing of a new or updated row before committing it to the DB"
  (fn [table _row] table))

(defmethod augment-row :inventory_item
  [_table row]
  (assoc row
         :date-added (java.time.LocalDateTime/now)
         :expiry_date (java.time.LocalDateTime/now)))

(defmethod augment-row :default
  [_table row]
  row)

(defn create
  "Create a new row"
  [db table row]
  (insert db table (augment-row table row)))

(defn edit
  "Update a row"
  [db table id row]
  (update-row db table id (augment-row table row)))

(defn- fetch-all-suppliers-for-fud-item-sql
  [fud-item-id]
  (-> (h/select :s.supplier_id :s.supplier_name :s.website :s.email :s.telephone :s.address :s.notes)
      (h/from [:fud.fud_item :i] [:fud.supplier :s] [:fud.fud_item_supplier :fs]) 
      (h/where :and [:= :i.fud_item_id fud-item-id] [:= :i.fud_item_id :fs.fud_item_id] [:= :s.supplier_id :fs.supplier_id])
      (sql/format {:pretty true})))

(defn fetch-all-suppliers-for-fud-item
  [db fud-item-id]
  (try
    (jdbc/execute! db (fetch-all-suppliers-for-fud-item-sql fud-item-id))
    (catch Exception e
      (u/log ::fetch-all-suppliers-for-fud-item :message (ex-message e) :fud-item-id fud-item-id)
      {:error {:status 500}})))

(defn- add-supplier-for-fud-item-sql
  [fud-item-id supplier-id]
  (-> (h/insert-into :fud.fud_item_supplier)
      (h/values [{:fud_item_id fud-item-id :supplier_id supplier-id}])
      (h/returning :fud_item_id :supplier_id)
      (sql/format {:pretty true})))

(defn add-supplier-for-fud-item 
  [db fud-item-id supplier-id]
  (try
    (jdbc/execute-one! db (add-supplier-for-fud-item-sql fud-item-id supplier-id))
    (catch Exception e
      (u/log ::add-supplier-for-fud-item :message (ex-message e) :fud-item-id fud-item-id :supplier-id supplier-id)
      {:error {:status 500}})))

(defn- delete-supplier-for-fud-item-sql
  [fud-item-id supplier-id]
  (-> (h/delete-from :fud.fud_item_supplier)
      (h/where :and [:= :fud_item_id fud-item-id] [:= :supplier_id supplier-id])
      (sql/format {:pretty true})))

(defn delete-supplier-for-fud-item
  [db fud-item-id supplier-id]
  (try
    (jdbc/execute-one! db (delete-supplier-for-fud-item-sql fud-item-id supplier-id))
    (catch Exception e
      (u/log ::delete-supplier-for-fud-item :message (ex-message e) :fud-item-id fud-item-id :supplier-id supplier-id)
      {:error {:status 500}})))

;; (defn- fetch-all-fud-item-categories-sql 
;;   [fud-item-id]
;;   (-> (h/select :fc.fud_category_id :fc.fud_category_name :fc.notes)
;;       (h/from [:fud.fud_item_category :fic] [:fud.fud_category :fc])
;;       (h/where :and [:= :fic.fud_item_id fud-item-id] [:= :fic.fud_category_id :fc.fud_category_id])
;;       (sql/format {:pretty true})))

;; (defn fetch-all-fud-item-categories 
;;   [db fud-item-id]
;;   (try 
;;     (jdbc/execute! db (fetch-all-fud-item-categories-sql fud-item-id))
;;     (catch Exception e
;;       (u/log ::fetch-all-fud-item-categories :message (ex-message e) :fud-item-id fud-item-id)
;;       {:error {:status 500}})))

;; (defn- fetch-all-fud-items-for-categories-sql 
;;   [fud-category-ids]
;;   (-> (h/select :fi.fud_item_id :fi.fud_type_id :fi.brand_id :fi.unit_qty :fi.unit_id :fi.notes)
;;       (h/from [:fud.fud_item_category :fic] [:fud.fud_item :fi]) 
;;       (h/where :and [:fic.fud_category_id :in fud-category-ids] [:= :fic.fud_item_id :fi.fud_item_id])
;;       (sql/format {:pretty true})))

;; (defn fetch-all-fud-categories-for-items 
;;   [db fud-category-ids]
;;   (try 
;;     (jdbc/execute! db (fetch-all-fud-items-for-categories-sql fud-category-ids))
;;     (catch Exception e
;;       (u/log ::fetch-all-fud-item-categories :message (ex-message e) :fud-category-ids fud-category-ids)
;;       {:error {:status 500}})))

;; (defn- add-fud-items-to-category-sql
;;   [])

;; (defn add-fud-items-to-category 
;;   [fud-category-id fud-item-ids]
;;   (try
;;     (jdbc/execute-one!db (add-fud-items-to-category-sql))
;;     (catch Exception e
;;       (u/log ::add-fud-items-to-category :message (ex-message e) :fud-category-id fud-category-id :fud-item-ids fud-item-ids)
;;       {:error {:status 500}})))

(comment 
  
  (fetch-all-fud-item-categories-sql  9)


)