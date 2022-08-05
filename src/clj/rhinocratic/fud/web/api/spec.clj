(ns rhinocratic.fud.web.api.spec
  (:require
   [clojure.spec.alpha :as s]))

(s/def ::nilable-string 
       (some-fn nil? string?))
(s/def ::nilable-pos-int 
       (some-fn nil? pos-int?))

(s/def ::name string?)
(s/def ::website ::nilable-string)
(s/def ::email ::nilable-string)
(s/def ::telephone ::nilable-string)
(s/def ::address ::nilable-string)
(s/def ::notes ::nilable-string)
(s/def ::low_stock_warning_level ::nilable-pos-int)
(s/def ::expiry_day ::nilable-pos-int)
(s/def ::expiry_month pos-int?)
(s/def ::expiry_year pos-int?)
(s/def ::brand_name ::name)
(s/def ::supplier_name ::name)
(s/def ::category_name ::name)
(s/def ::fud_item_name ::name)

(s/def ::brand
  (s/keys :req-un [::brand_name]
          :opt-un [::notes]))

(s/def ::supplier
  (s/keys :req-un [::supplier_name]
          :opt-un [::website ::email ::telephone ::address ::notes]))

(s/def ::category
  (s/keys :req-un [::category_name]
          :opt-un [::notes]))

(s/def ::fud-item
  (s/keys :req-un [::fud_item_name ::brand_id ::supplier_id]
          :opt-un [::low_stock_warning_level ::notes]))

(s/def ::inventory-item
  (s/keys :req-un [::fud_item_id ::expiry_month ::expiry_year]
          :opt-un [::expiry_day]))
