(ns rhinocratic.fud.web.api.routes
  (:require
   [clojure.spec.alpha :as s]
   [rhinocratic.fud.web.api.handlers :as h]
   [rhinocratic.fud.web.api.spec :as api-spec]))

(s/def ::role #{:admin})

(defn routes
  [db]
  [["/brands" {:get {:handler (partial #'h/fetch-all db :brand)
                     :summary "List all brands"}}]
   ["/suppliers" {:get {:handler (partial #'h/fetch-all db :supplier)
                        :summary "List all suppliers"}
                  :post {:handler (partial #'h/create db :supplier ::supplier)
                         :parameters {:body {:supplier ::api-spec/supplier}}
                         :summary "Create a new supplier"}}]
   ["/fud-categories" {:get {:handler (partial #'h/fetch-all db :fud_category)
                             :summary "List all fud categories"}}]
   ["/fud-items" {:get {:handler (partial #'h/fetch-all db :fud_item)
                        :summary "List all fud items"}}] 
   ["/brands/:id" {:name ::brand
                   :get {:handler (partial #'h/fetch-one db :brand)
                         :summary "Fetch a brand by ID"
                         :parameters {:path {:id int?}}}}]
   ["/suppliers/:id" {:name ::supplier
                      :get {:handler (partial #'h/fetch-one db :supplier)
                            :summary "Fetch a supplier by ID"
                            :parameters {:path {:id int?}}}
                      :put {:handler (partial #'h/edit db :supplier)
                            :summary "Delete a supplier by ID" 
                            :parameters {:path {:id int?}}}
                      :delete {:handler (partial #'h/delete db :supplier)
                               :summary "Delete a supplier by ID"
                               :parameters {:path {:id int?}}}}]
   ["/fud-categories/:id" {:name ::fud-category
                           :get {:handler (partial #'h/fetch-one db :fud-category)
                                 :summary "Fetch a fud category by ID"
                                 :parameters {:path {:id int?}}}}]
   ["/fud-items/:id" {:name ::fud-item
                      :get {:handler (partial #'h/fetch-one db :fud-item)
                            :summary "Fetch a fud item by ID"
                            :parameters {:path {:id int?}}}}]])

(comment 
  
     ;; {:spec (s/merge (s/keys :req [::roles]) ::rs/default-data)}

  )