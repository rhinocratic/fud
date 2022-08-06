(ns rhinocratic.fud.components.page-wrapper
  (:require
   [reagent.core :as r]
   #_[rhinocratic.fud.components.navbar :as nav]))

(defn wrap
  [component]
  [:div.top
   [:section.hero
    [:div.hero-body
     [:p.title "Fud"]]]
   #_[nav/navbar]
   [:section
    [:div.container.is-max-widescreen
     [:div component]]]])
