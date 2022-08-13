(ns rhinocratic.fud.web.api.spec
  (:require 
   [clojure.spec.alpha :as s]
   [clojure.string :as str])
  (:import
   [java.net URL]
   [com.google.i18n.phonenumbers
    PhoneNumberUtil
    PhoneNumberUtil$PhoneNumberFormat
    NumberParseException]))

(def phone-utils 
  "Telephone number parsing and formatting utilities"
  (delay (PhoneNumberUtil/getInstance)))

(defn conform-url 
  [s]
  (try 
    (-> s (URL.) str)
    (catch Exception _ 
      :clojure.spec.alpha/invalid)))

(defn conform-telephone-number
  [s]
  (try 
    (as-> s $
      (.parse @phone-utils $ "GB")
      (.format @phone-utils $ PhoneNumberUtil$PhoneNumberFormat/INTERNATIONAL))
    (catch Exception _
      :clojure.spec.alpha/invalid)))

(defn email?
  [s]
  (let [email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$"]
    (re-matches email-regex s)))

(s/def ::req-pos-int pos-int?)
(s/def ::opt-pos-int (s/nilable pos-int?))
(s/def ::req-str (s/and string? (complement str/blank?)))
(s/def ::opt-str (s/or :name string? :blank str/blank?))
(s/def ::url (s/conformer conform-url))
(s/def ::email (s/and string? email?))
(s/def ::telephone-number (s/conformer conform-telephone-number))

;; Brand
(s/def :brand/brand_name string?)
(s/def :brand/notes string?)
(s/def ::brand
  (s/keys :req [:brand/brand_name]
          :opt [:brand/notes]))

;; Supplier
(s/def :supplier/supplier_name ::req-str)
(s/def :supplier/email ::email)
(s/def :supplier/telephone ::telephone-number)
(s/def :supplier/address ::opt-str)
(s/def :supplier/website ::url)
(s/def :supplier/notes ::opt-str)
(s/def ::supplier
  (s/keys :req [:supplier/supplier_name]
          :opt [:supplier/website :supplier/email :supplier/telephone :supplier/address :supplier/notes]))

;; Fud Category
(s/def :fud_category/fud_category_name ::req-str)
(s/def :fud_category/notes ::opt-str)
(s/def ::fud_category
  (s/keys :req [:fud_category/fud_category_name]
          :opt [:fud_category/notes]))

;; Fud Type
(s/def :fud_type/fud_type_name ::req-str)
(s/def :fud_type/low_stock_qty ::req-pos-int)
(s/def :fud_type/low_stock_unit_id ::req-pos-int)
(s/def :fud_type/notes ::opt-str)
(s/def ::fud_type
  (s/keys :req [:fud_type/fud_type_name (or (and :fud_type/low_stock_qty :fud_type/low_stock_unit_id)
                                            (and (not :fud_type/low_stock_qty) (not :fud_type/low_stock_unit_id)))]
          :opt [:fud_type/notes]))

;; Fud Item
(s/def :fud_item/fud_type_id ::req-pos-int)
(s/def :fud_item/brand_id ::req-pos-int)
(s/def :fud_item/unit_qty ::req-pos-int)
(s/def :fud_item/unit_id ::req-pos-int)
(s/def :fud_item/notes ::opt-str)
(s/def ::fud_item
  (s/keys :req [:fud_item/fud_type_id :fud_item/brand_id :fud_item/unit_qty :fud_item/unit_id]
          :opt [:fud_item/notes]))

;; Inventory Item
(s/def :inventory_item/fud_item_id ::req-pos-int)
(s/def :inventory_item/expiry_month ::req-pos-int)
(s/def :inventory_item/expiry_year ::req-pos-int)
(s/def :inventory_item/expiry_day ::opt-pos-int)
(s/def ::inventory_item
  (s/keys :req [:inventory_item/fud_item_id :inventory_item/expiry_month :inventory_item/expiry_year]
          :opt [:inventory_item/expiry_day]))

;; Unit
(s/def :unit/unit_name ::req-str)
(s/def ::unit
  (s/keys :req [:unit/unit_name]))
