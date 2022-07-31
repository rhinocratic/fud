(ns rhinocratic.fud.logging
  (:require 
   [clojure.string :as str]
   [com.brunobonacci.mulog :as u]
   [ring.logger :as ring-logger])
  (:import java.util.Base64))

(defn ring-logger->mulog
  "Forward ring-logger messages to mulog (rather than the default of clojure.tools.logging),
   transforming the message to JSON"
  [no-logging-on]
  (let [log-it? (->> no-logging-on
                     (map #(fn [msg] (str/starts-with? (:uri msg) %)))
                     (apply some-fn)
                     (comp not))]
    (fn [{:keys [throwable message]}]
      (when (log-it? message)
        (u/log :message message :throwable (ex-message throwable))))))

(defn extract-username
  "Decode the Base64-encoded part of an HTTP Basic Authorization header and throw away the password part."
  [auth-header]
  (let [encoded (-> (re-find #"Basic ([^\s]+)" auth-header)
                    second)]
    (if encoded
      (->> encoded
           (.decode (Base64/getDecoder))
           (String.)
           (#(subs % 0 (str/index-of % ":"))))
      "")))

(defn transform-auth-header
  "Pick out information from selected headers"
  [headers]
  (cond-> (select-keys headers ["x-forwarded-for" "authorization"])
    (headers "authorization") (-> (dissoc "authorization")
                                  (assoc "username" (extract-username (headers "authorization"))))))

(defn transform-ring-message
  "Add selected headers into the top level of the message map"
  [{{:keys [headers]} :message :as log-record}]
  (-> log-record
      (update :message dissoc :headers)
      (update :message merge (transform-auth-header headers))))

(defn ring-logger
  "Log ring request/response details. Optionally suppress logging on a given set of URL prefixes."
  ([handler]
   (ring-logger handler {:no-logging-on []}))
  ([handler {:keys [no-logging-on]}]
   (ring-logger/wrap-with-logger handler {:log-fn (ring-logger->mulog no-logging-on)
                                          :request-keys [:request-method
                                                         :uri
                                                         :server-name
                                                         :ring.logger/type
                                                         :status
                                                         :ring.logger/ms
                                                         :headers]
                                          :transform-fn transform-ring-message})))

(defn start-publishers!
  [publisher-configs]
  (doall
   (for [conf publisher-configs]
     (let [publisher (u/start-publisher! conf)]
       (u/log ::started-publisher :message (format "Started log publisher %s" (:type conf)))
       publisher))))

(defn start-logging!
  [{:keys [global-context publishers]}]
  (u/set-global-context! global-context)
  (start-publishers! publishers))

(defn stop-logging! 
  [publishers]
  (doseq [publisher publishers]
    (u/log ::stopping-publisher :message (format "Stopping log publisher %s" (type publisher)))
    (publisher)))
