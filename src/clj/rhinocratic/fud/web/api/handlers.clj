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
  (let [items (q/select-all db table)]
    (with-status items)))

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

(defn fetch-all-suppliers-for-fud-item 
  [db req]
  (let [fud-item-id (get-in req [:parameters :path :fud-item-id])
        items (q/fetch-all-suppliers-for-fud-item db fud-item-id)]
    (with-status items)))

(defn add-supplier-for-fud-item
  [db req]
  (let [fud-item-id (get-in req [:parameters :path :fud-item-id])
        supplier-id (get-in req [:parameters :body :supplier-id])
        item-supplier (q/add-supplier-for-fud-item db fud-item-id supplier-id)]
    (with-status item-supplier)))

(defn delete-supplier-for-fud-item 
  [db req]
  (let [fud-item-id (get-in req [:parameters :path :id])
        supplier-id (get-in req [:parameters :path :supplier-id])
        item-supplier (q/delete-supplier-for-fud-item db fud-item-id supplier-id)]
    (with-status item-supplier)))

;; (defn fetch-all-fud-item-categories
;;   [db req]
;;   (let [fud-category-id (get-in req [:parameters :path :fud-category-id])
;;         categories (q/fetch-all-categories-for-fud-items db [fud-category-id])]
;;     (with-status categories)))

;; (defn add-fud-item-to-category 
;;   [db req]
;;   (let [fud-category-id (get-in req [:parameters :path :fud-category-id])
;;         fud-item-id (get-in req [:parameters :body :fud-item-id])
;;         item-category (q/add-fud-item-to-category db fud-category-id fud-item-id)]
;;     (with-status item-category)))

;; (defn delete-fud-item-from-category 
;;   [db req]
;;   (let [fud-category-id (get-in req [:parameters :path :fud-category-id])
;;         fud-item-id (get-in req [:parameters :path :fud-item-id])
;;         item-category (q/delete-fud-item-from-category db fud-category-id fud-item-id)]
;;     (with-status item-category)))

;; (defn fetch-all-fud-category-items 
;;   [db req]
;;   (let [fud-category-id (get-in req [:parameters :path :fud-category-id])
;;         items (q/fetch-all-fud-categories-for-items db [fud-category-id])]
;;     (with-status items)))