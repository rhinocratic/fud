(ns rhinocratic.fud.web.api.handlers
  (:require 
   [reitit.core :as r]
   [rhinocratic.fud.db.queries :as q]))

(defn error? 
  [value]
  (contains? value :error))

(defn with-status
  "Attach a status code to the result of a handler operation"
  ([item]
   (with-status item 200))
  ([item status]
   (cond
     (error? item) (:error item)
     (nil? item) {:status 404}
     :else {:status status :body item})))

(defn fetch-all
  "Fetch all rows from the DB"
  [db table _req]
  (let [item (q/select-all db table)]
    (with-status item)))

(defn fetch-one 
  "Fetch a single item from the DB"
  [db table req]
  (let [id (get-in req [:parameters :path :id])
        item (q/select-one db table id)]
    (with-status item)))

(defn delete
  "Delete a single item from the DB"
  [db table req]
  (let [id (get-in req [:parameters :path :id])
        item (q/delete db table id)]
    (with-status item)))

(defn location
  [table item router reverse-route-name]
  (->> item
       ((q/primary-key table))
       (assoc {} :id)
       (r/match-by-name router reverse-route-name)
       :path))

(defn with-location 
  [item location]
  (assoc item :headers {"Location" location}))

(defn create 
  "Create a new item in the DB"
  [db table reverse-route-name {:keys [body-params] ::r/keys [router]}]
  (let [row (table body-params)
        item (q/create db table row)
        location (location table item router reverse-route-name)]
    (-> item
        (with-status 201)
        (with-location location))))

(defn edit
  "Update a single item in the DB"
  [db table {:keys [body-params] :as req}]
  (let [row (table body-params)
        id (get-in req [:parameters :path :id])
        item (q/edit db table id row)]
    (with-status item)))
