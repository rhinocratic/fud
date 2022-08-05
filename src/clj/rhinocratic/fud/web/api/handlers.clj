(ns rhinocratic.fud.web.api.handlers
  (:require 
   [com.brunobonacci.mulog :as u]
   [rhinocratic.fud.db.queries :as q]))

(defn all-items
  [db table _req]
  {:status 200 :body (q/select-all db table)})

(defn item-by-id 
  [db table req]
  (let [id (get-in req [:parameters :path :id])
        item (q/select-by-id db table id)]
    (if item 
      {:status 200 :body item}
      {:status 404})))

(defn suppliers-handler-post
  [{:keys [db]}]
  {:status 200 :body (q/new-supplier db nil)}) ;; QQQQ 