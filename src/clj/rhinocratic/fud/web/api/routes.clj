(ns rhinocratic.fud.web.api.routes
  (:require
   [clojure.spec.alpha :as s]
   [rhinocratic.fud.web.api.handlers :as h]))

(s/def ::role #{:admin})

(defn table-spec 
  [table]
  (keyword "rhinocratic.fud.web.api.spec" (name table)))

(defn list-handlers
  [db table singular plural]
  {:get {:handler (partial #'h/fetch-all db table)
         :summary (format "List all %s" plural)}
   :post {:handler (partial #'h/create db table (table-spec table))
          :parameters {:body {table (table-spec table)}}
          :summary (format "Create a new %s" singular)}})

(defn readonly-list-handler
  [db table plural]
  {:get {:handler (partial #'h/fetch-all db table)
         :summary (format "List all %s" plural)}})

(defn get-put-delete-handlers 
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

(defn food-item-suppliers-handler
  [db]
  {:parameters {:path {:id int?}}
   :get {:handler (partial #'h/fetch-suppliers-for-fud-item db)
         :summary "Fetch all suppliers of a given fud item"}
   :post {:handler (partial #'h/add-supplier-for-fud-item db)
          :parameters {:body {:supplier_id int?}}
          :summary "Add an existing supplier to the list of suppliers of a given fud item"}})

(defn routes
  [db]
  [["/brands" (list-handlers db :brand "brand" "brands")]
   ["/suppliers" (list-handlers db :supplier "supplier" "suppliers")]
   ["/fud-categories" (list-handlers db :fud_category "fud category" "fud categories")]
   ["/fud-types" (list-handlers db :fud_type "fud type" "fud types")]
   ["/fud-items" (list-handlers db :fud_item "fud item" "fud items")] 
   ["/inventory-items" (list-handlers db :inventory_item "inventory item" "inventory items")]
   ["/units" (readonly-list-handler db :unit "units")]

   ["/brands/:id" (get-put-delete-handlers db :brand "a brand")]
   ["/suppliers/:id" (get-put-delete-handlers db :supplier "a supplier")] 
   ["/fud-categories/:id" (get-put-delete-handlers db :fud_category "a fud category")] 
   ["/fud-types/:id" (get-put-delete-handlers db :fud_type "a fud type")] 
   ["/fud-items/:id" (get-put-delete-handlers db :fud_item "a fud item")] 
   ["/inventory-items/:id" (get-put-delete-handlers db :inventory_item "an inventory item")]

   ["/fud-items/:id/suppliers" (food-item-suppliers-handler db)]
   ])

(comment 
  
     ;; {:spec (s/merge (s/keys :req [::roles]) ::rs/default-data)}

  )