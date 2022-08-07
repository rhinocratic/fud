(ns rhinocratic.fud.components.pagination
  (:require
   [reagent.core :as r]
   [goog.string :as gstr]))

(defn pagination-ellipsis 
  "Placeholder indicating the elision of more than one page"
  []
  [:li 
   [:span.pagination-ellipsis (gstr/unescapeEntities "&hellip;")]])

(defn pagination-link
  "Link to the nth page"
  [n curr-item]
  [:li [:a {:class (str "pagination-link" (when (= n @curr-item) " is-current"))
            :aria-label (str "Go to page " n)
            :on-click (fn [] (reset! curr-item n))}
        (str n)]])

(defn pagination-prev 
  "Select the previous page"
  [min-item curr-item]
  (let [disabled (= min-item @curr-item)]
    [:a.pagination-previous 
     {:aria-label "Previous page"
      :on-click (fn [] (when-not disabled (swap! curr-item dec)))
      :disabled disabled} "<"]))

(defn pagination-next 
  "Select the next page"
  [max-item curr-item]
  (let [disabled (= max-item @curr-item)]
    [:a.pagination-next
     {:aria-label "Next page"
      :on-click (fn [] (when-not disabled (swap! curr-item inc)))
      :disabled disabled} ">"]))

(defn calc-mid
 "Calculate the indices of the page numbers in the 'middle' section of the paginator" 
  [min-item max-item num-links curr-item]
  (let [half-num-links (int (/ num-links 2))
        mid-len (if (or (<= (- curr-item min-item) half-num-links)
                        (<= (- max-item curr-item) half-num-links))
                  (- num-links 3)
                  (- num-links 4))
        low (max (inc min-item) (- curr-item (- half-num-links 2)))
        low (cond-> low (= low (+ 2 min-item)) dec)
        low (min low (- max-item mid-len))
        high (+ low mid-len)]
    (range low high)))

(defn item-numbers
  "Calculate the indices of the page numbers to be shown, truncating at the start and/or end if necessary."
  [num-items num-links min-item curr-item]
  (let [max-item (dec (+ min-item num-items))
        num-links (min num-items num-links)
        mid (if (> num-links 6)
              (calc-mid min-item max-item num-links curr-item)
              (range (inc min-item) max-item))]
    (vec (concat [min-item] mid [max-item]))))

(defn pagination-links
  "Create pagination links, replacing gaps in the sequence by ellipses"
  [num-items min-item num-links curr-item]
  (let [nums (item-numbers num-items min-item num-links @curr-item)]
    (->> (conj nums num-items)
         (partition 2 1)
         (mapcat #(vector 
                   [pagination-link (first %) curr-item]
                   (when-not (#{0 -1} (apply - %))
                     [pagination-ellipsis])))
         (map-indexed #(with-meta %2 {:key %1}))
          doall)))

(defn paginator
  "Paginator component over the given items.
   Options:
    num-links - the total number of links and ellipses that should appear in the paginator
    min-item  - the smallest item number that should appear as a link (usually 0 or 1)
    curr-item - a ratom containing the number of the currently selected link.  If not supplied, uses an internal ratom to maintain state -
                this setting just allows paginators at the top & bottom of the page to share state.
    on-change - a function that accepts an integer (the selected page number) and performs some action to update the paginated content"
  [{:keys [num-links min-item curr-item on-change] :as opts} items]
  (let [num-items (count items)
        curr-item (or curr-item (r/atom min-item))
        opts' (remove #{:num-links :min-item :curr-item :on-change} opts)]
    (add-watch curr-item :page-watcher  (fn [_ _ old-val new-val] (when-not (= old-val new-val)
                                                                    (on-change new-val))))
    (fn [items]
      (cond
        (zero? num-items) [:div.title.is-5.has-text-centered  "No results found"]
        ;; (= 1 num-items) [:div.title.is-5.has-text-centered "1 result found"]
        :else  [:div.columns opts
                [:div.column
                 [:nav.pagination.is-centered {:role "navigation" :aria-label "pagination"}
                  [pagination-prev min-item curr-item]
                  [pagination-next (+ min-item (dec num-items)) curr-item]
                  [:ul.pagination-list
                   (pagination-links num-items num-links min-item curr-item)]]]]))))

