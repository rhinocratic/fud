(ns rhinocratic.fud.web.api.handlers
  (:require 
   [rhinocratic.fud.db.queries :as q]))

(defn root-handler
  [{:keys [db]}]
  {:status 200 :body "Root of the API"})

(defn all-suppliers 
  [{:keys [db]}]
  {:status 200 :body (str (q/all-suppliers db))})

(defn all-fud-items
  [{:keys [db]}]
  {:status 200 :body (str (q/all-fud-items db))})

(defn fud-item 
  [{:keys [db item-id]}]
  {:status 200 :body (str (q/fud-item db item-id))})

(defn suppliers-handler-post
  [{:keys [db]}]
  {:status 200 :body (q/new-supplier db nil)}) ;; QQQQ