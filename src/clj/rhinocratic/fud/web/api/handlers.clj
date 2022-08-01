(ns rhinocratic.fud.web.api.handlers
  (:require 
   [rhinocratic.fud.db.queries :as q]))

(defn root-handler
  [_]
  {:status 200 :body "Root of the API"})

(defn suppliers-handler-get 
  [{:keys [db]}]
  {:status 200 :body (str (q/all-suppliers db))})

(defn fud-items-handler-get
  [{:keys [db]}]
  {:status 200 :body (str (q/all-fud-items db))})

(defn suppliers-handler-post
  [{:keys [db]}]
  {:status 200 :body (q/new-supplier db nil)}) ;; QQQQ