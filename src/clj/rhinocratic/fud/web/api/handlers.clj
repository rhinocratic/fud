(ns rhinocratic.fud.web.api.handlers
  (:require 
   [reitit.core :as r]
   [com.brunobonacci.mulog :as u]
   [rhinocratic.fud.db.queries :as q]))

(defn all-items
  [db table _req]
  ;; (clojure.pprint/pprint _req)
  {:status 200 :body (q/select-all db table)})

(defn item-by-id 
  [db table {:keys [path-params]}]
  (let [item (q/select-by-id db table (:id path-params))]
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
