(ns rhinocratic.fud.web.api.routes
  (:require
   [clojure.spec.alpha :as s]
   [rhinocratic.fud.web.api.handlers :as h]
   [rhinocratic.fud.web.api.spec :as api-spec]))

(s/def ::role #{:admin})

(defn table-spec 
  [table]
  (keyword "rhinocratic.fud.web.api.spec" (name table)))

(defn all-rows-handlers
  [db table singular plural]
  {:get {:handler (partial #'h/fetch-all db table)
         :summary (format "List all %s" plural)}
   :post {:handler (partial #'h/create db table (table-spec table))
          :parameters {:body {table (table-spec table)}
          :summary (format "Create a new %s" singular)}}})

(defn single-row-handlers 
  [db table description]
  {:name       (keyword (str *ns*) (name table))
   :parameters {:path {:id int?}}
   :get        {:handler (partial #'h/fetch-one db table)
                :summary (format "Fetch %s by ID" description)}
   :put        {:handler (partial #'h/edit db table)
                :summary (format "Edit %s by ID" description)
                :parameters {:body {table (table-spec table)}}}
   :delete     {:handler (partial #'h/delete db table)
                :summary (format "Delete %s by ID" description)}})

(defn routes
  [db]
  [["/brands" (all-rows-handlers db :brand "brand" "brands")]
   ["/suppliers" (all-rows-handlers db :supplier "supplier" "suppliers")]
   ["/fud-categories" (all-rows-handlers db :fud_category "fud category" "fud categories")]
   ["/fud-types" (all-rows-handlers db :fud_type "fud type" "fud types")]
   ["/fud-items" (all-rows-handlers db :fud_item "fud item" "fud items")] 
   ["/inventory-items" (all-rows-handlers db :inventory_item "inventory item" "inventory items")]
   ["/brands/:id" (single-row-handlers db :brand "a brand")]
   ["/suppliers/:id" (single-row-handlers db :supplier "a supplier")] 
   ["/fud-categories/:id" (single-row-handlers db :fud_category "a fud category")] 
   ["/fud-types/:id" (single-row-handlers db :fud_type "a fud type")]
   ["/fud-items/:id" (single-row-handlers db :fud_item "a fud item")] 
   ["/inventory-items/:id" (single-row-handlers db :inventory_item "an inventory item")]])

(comment 
  
     ;; {:spec (s/merge (s/keys :req [::roles]) ::rs/default-data)}

  )