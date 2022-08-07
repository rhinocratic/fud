(ns rhinocratic.fud.main-page
  (:require
   [reagent.core :as r]
   [rhinocratic.fud.components.page-wrapper :as w]
   [rhinocratic.fud.components.tabs :as t]
   [rhinocratic.fud.components.inventory :as i]))

(defn main-page 
  []
  (w/wrap 
   [:div
    [t/fud-tabs]
    [i/inventory]]))