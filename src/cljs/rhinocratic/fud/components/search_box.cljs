(ns rhinocratic.fud.components.search-box)

(defn search-box
  []
  [:div.field
   [:p.control.has-icons-left.has-icons-right
    [:input.input {:placeholder "Search", :type "search"}]
    [:span.icon.is-small.is-left [:i.fa-solid.fa-magnifying-glass]]]])