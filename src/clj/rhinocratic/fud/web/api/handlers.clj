(ns rhinocratic.fud.web.api.handlers
  (:require 
   [com.brunobonacci.mulog :as u]
   [rhinocratic.fud.db.queries :as q]))

(defn all-items
  [db table _req]
  {:status 200 :body (q/select-all db table)})

(defn item-by-id 
  [db table {:keys [path-params]}]
  (let [item (q/select-by-id db table (:id path-params))]
    (if item 
      {:status 200 :body item}
      {:status 404})))

(defn new-item 
  [db table {:keys [body-params]}]
  (println "Creating a new item")
  (println db)
  (println table)
  (println (table body-params))
  (let [row (table body-params)
        item (q/create-new db table row)]
    {:status 201 :body item}))
