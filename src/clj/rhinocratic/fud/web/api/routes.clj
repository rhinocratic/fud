(ns rhinocratic.fud.web.api.routes
  (:require
   [clojure.spec.alpha :as s]
   [rhinocratic.fud.web.api.handlers :as h]
   [rhinocratic.fud.web.api.spec :as api-spec]))

(s/def ::role #{:admin})

(defn all-rows-handlers
  [db table spec singular plural]
  {:get {:handler (partial #'h/fetch-all db table)
         :summary (format "List all %s" plural)}
   :post {:handler (partial #'h/create db :supplier ::supplier)
          :parameters {:body {table spec}
          :summary (format "Create a new %s" singular)}}})

(defn single-row-handlers 
  [db table description]
  {:name       (keyword *ns* table)
   :parameters {:path {:id int?}}
   :get        {:handler (partial #'h/fetch-one db table)
                :summary (format "Fetch %s by ID" description)}
   :put        {:handler (partial #'h/edit db table)
                :summary (format "Delete %s by ID" description)}
   :delete     {:handler (partial #'h/delete db table)
                :summary (format "Delete %s by ID" description)}})

(defn routes
  [db]
  [["/brands" (all-rows-handlers db :brand ::api-spec/brand "brand" "brands")]
   ["/suppliers" (all-rows-handlers db :supplier ::api-spec/supplier "supplier" "supplier")]
   ["/fud-categories" (all-rows-handlers db :fud_category ::api-spec/fud_category "fud category" "fud categories")]
   ["/fud-items" (all-rows-handlers db :fud_item ::api-spec/fud_item "fud item" "fud items")] 
   ["/inventory-items" (all-rows-handlers db :inventory_item ::api-spec/inventory_item "inventory item" "inventory items")]
   ["/brands/:id" (single-row-handlers db :brand "a brand")]
   ["/suppliers/:id" (single-row-handlers db :supplier "a supplier")] 
   ["/fud-categories/:id" (single-row-handlers db :fud_category "a fud category")] 
   ["/fud-items/:id" (single-row-handlers db :fud_item "a fud item")] 
   ["/inventory-items/:id" (single-row-handlers db :inventory_item "an inventory item")]])

(comment 
  
     ;; {:spec (s/merge (s/keys :req [::roles]) ::rs/default-data)}

  )