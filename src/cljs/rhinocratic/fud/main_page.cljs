(ns rhinocratic.fud.main-page
  (:require
   [reagent.core :as r]
   [rhinocratic.fud.components.page-wrapper :as w]))

(defn main-page 
  []
  (w/wrap 
   [:div "The main thingummy"]))