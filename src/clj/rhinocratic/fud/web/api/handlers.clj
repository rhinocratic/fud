(ns rhinocratic.fud.web.api.handlers
  (:require 
   [reitit.core :as r]
   [com.brunobonacci.mulog :as u]
   [rhinocratic.fud.db.queries :as q]))

(defn all-items
  [db table _req]
  {:status 200 :body (q/select-all db table)})

(defn item-by-id 
  [db table req]
  (let [item (q/select-by-id db table (get-in req [:parameters :path :id]))]
    (if item 
      {:status 200 :body item}
      {:status 404})))

(defn delete-item 
  [db table id]
  (let [item (q/delete-by-id db table id)]
    (if item
      {:status 200 :body item}
      {:status 404})))

(defn new-item 
  [db table reverse-route-name {:keys [body-params] ::r/keys [router]}]
  (let [row (table body-params)
        new-item-id (-> (q/create-new db table row)
                        vals
                        first)
        new-item-location (->> {:id new-item-id}
                               (r/match-by-name router reverse-route-name)
                               :path)]
    {:status 201 :headers {"Location" new-item-location}}))
