(ns rhinocratic.fud.components.tabs)

(defn tab-item 
  [text icon active]
  [:li (when active {:class "is-active"})
   [:a
    [:span.icon [:i.fas {:class icon :aria-hidden true}]]
    [:span text]]])

(defn tab-group
  [& tabs]
  [:div.tabs.is-toggle.is-fullwidth.is-large.m-5
   [:ul tabs]])

(defn fud-tabs 
  []
  [tab-group
   [tab-item "What's in" "fa-jar" true]
   [tab-item "What's used" "fa-wine-glass-empty" false]
   [tab-item "What's waste" "fa fa-trash" false]])
