(ns rhinocratic.fud.components.inventory
  (:require
   [rhinocratic.fud.components.pagination :as p]))

(defn table 
  [opts headers rows]
  [:table.table opts
   [:thead
    [:tr
     (for [header headers]
       [:td 
        [:strong header]])]]
   [:tbody 
   (for [row rows]
     [:tr (for [item row]
            [:td item])])]])

(defn edit-icons
  []
  [:div])

(defn inventory 
  []
  [:div.m-5
   [table {:class ["is-fullwidth" "is-striped" "is-hoverable"]}
    ["Item" "Qty" "Expiry Date" ""]
    [["Cream crackers" "2" "19/11/64" [edit-icons]]
     ["Carnaroli Rice" "3" "19/11/64" [edit-icons]]
     ["Salted Capers" "6" "19/11/64" [edit-icons]]
     ["Artichokes in oil" "2" "19/11/64" [edit-icons]]
     ["Sun-dried tomatoes in oil" "2" "19/11/64" [edit-icons]]]]
   [p/paginator {:num-links 7 :class ["is-three-quarters"] :min-item 1} (range 12)]])