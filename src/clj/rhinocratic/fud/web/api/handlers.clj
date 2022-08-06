(ns rhinocratic.fud.web.api.handlers
  (:require 
   [reitit.core :as r]
   [rhinocratic.fud.db.queries :as q]))

(defn fetch-all
  "Fetch all rows from the DB"
  [db table _req]
  {:status 200 :body (q/select-all db table)})

(defn fetch-one 
  "Fetch a single item from the DB"
  [db table req]
  (let [item (q/select-one db table (get-in req [:parameters :path :id]))]
    (if item 
      {:status 200 :body item}
      {:status 404})))

(defn delete 
  "Delete a single item from the DB"
  [db table id]
  (let [item (q/delete db table id)]
    (if item
      {:status 200 :body item}
      {:status 404})))

(defn create 
  "Create a new item in the DB"
  [db table reverse-route-name {:keys [body-params] ::r/keys [router]}]
  (let [row (table body-params)
        new-item-id (-> (q/create db table row)
                        ((q/primary-key table)))
        new-item-location (->> {:id new-item-id}
                               (r/match-by-name router reverse-route-name)
                               :path)]
    {:status 201 :headers {"Location" new-item-location}}))

(defn edit
  "Update a single item in the DB"
  [db table id  {:keys [body-params]}]
  (let [row (table body-params)
        item (q/edit db table id row)]
    (if item
      {:status 200 :body item}
      {:status 404})))
