(ns rhinocratic.fud.web.api.routes
  (:require
   [clojure.spec.alpha :as s]
   [rhinocratic.fud.web.api.handlers :as h]))

(s/def ::role #{:admin})

(defn routes
  [db]
  [["/brands" {:get {:handler (partial #'h/all-items db :brand)
                     :summary "List all brands"}}]
   ["/suppliers" {:get {:handler (partial #'h/all-items db :supplier)
                        :summary "List all suppliers"}}]
   ["/fud-categories" {:get {:handler (partial #'h/all-items db :fud_category)
                             :summary "List all fud categories"}}]
   ["/fud-items" {:get {:handler (partial #'h/all-items db :fud_item)
                        :summary "List all fud items"}}] 
   ["/brands/:id" {:get {:handler (partial #'h/item-by-id db :brand)
                         :summary "Fetch a brand by ID"
                         :parameters {:path {:id int?}}}}]
   ["/suppliers/:id" {:get {:handler (partial #'h/item-by-id db :supplier)
                            :summary "Fetch a supplier by ID"
                            :parameters {:path {:id int?}}}}]
   ["/fud-categories/:id" {:get {:handler (partial #'h/item-by-id db :fud-category)
                                 :summary "Fetch a fud category by ID"
                                 :parameters {:path {:id int?}}}}]
   ["/fud-items/:id" {:get {:handler (partial #'h/item-by-id db :fud-item)
                            :summary "Fetch a fud item by ID"
                            :parameters {:path {:id int?}}}}]]
   #_{:spec (s/merge (s/keys :req [::roles]) ::rs/default-data)})