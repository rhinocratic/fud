(ns rhinocratic.fud.web.api.spec
  (:require
   [clojure.spec.alpha :as s]))

(s/def ::nilable-string 
       (some-fn nil? string?))
(s/def ::nilable-pos-int 
       (some-fn nil? pos-int?))

(s/def ::id pos-int?)
(s/def ::name string?)
(s/def ::website ::nilable-string)
(s/def ::email ::nilable-string)
(s/def ::telephone ::nilable-string)
(s/def ::address ::nilable-string)
(s/def ::notes ::nilable-string)
(s/def ::expiry_day ::nilable-pos-int)
(s/def ::expiry_month pos-int?)
(s/def ::expiry_year pos-int?)
(s/def ::brand_id ::id)
(s/def ::brand_name ::name)
(s/def ::supplier_name ::name)
(s/def ::fud_category_name ::name)
(s/def ::fud_type_name ::name)
(s/def ::low_stock_qty nat-int?)
(s/def ::low_stock_unit_id ::id)
(s/def ::fud_item_id ::id)
(s/def ::fud_type_id ::id)
(s/def ::unit_qty pos-int?)
(s/def ::unit_id ::id)

(s/def ::brand
  (s/keys :req-un [::brand_name]
          :opt-un [::notes]))

(s/def ::supplier
  (s/keys :req-un [::supplier_name]
          :opt-un [::website ::email ::telephone ::address ::notes]))

(s/def ::fud_category
  (s/keys :req-un [::fud_category_name]
          :opt-un [::notes]))

(s/def ::fud_type
  (s/keys :req-un [::fud_type_name]
          :opt-un [::low_stock_qty ::low_stock_unit_id ::notes]))

(s/def ::fud_item
  (s/keys :req-un [::fud_item_id ::fud_type_id ::brand_id ::unit_qty ::unit_id]
          :opt-un [::notes]))

(s/def ::inventory_item
  (s/keys :req-un [::fud_item_id ::expiry_month ::expiry_year]
          :opt-un [::expiry_day]))

(s/def ::unit
  (s/keys :req-un [::unit_name]))

(defn for-table
  "Retrieve the spec for the given table"
  [table]
  (s/get-spec (keyword (str *ns*) (name table))))
