(ns rhinocratic.fud.site
  (:require
   [reagent.core :as r]
   [reagent.dom :as rd]
   [rhinocratic.fud.main-page :as m]))

(enable-console-print!)

(defonce app-state (r/atom {:text "Hello again, World!"}))

(defn app-element 
  []
  (.getElementById js/document "app"))

(defn mount
  [component context]
  (rd/render component context))

(defn mount-app-element 
  [component]
  (when-let [app-elt (app-element)]
    (mount component app-elt)))

(mount-app-element [m/main-page])