(ns matthiasn.systems-toolbox-observer.store
  "Store component for doing meaningful stuff with firehose messages."
  (:require [clojure.pprint :as pp]
            [clojurewerkz.elastisch.rest.document :as esd]
            [matthiasn.systems-toolbox.component :as st]
            [clojure.tools.logging :as log]
            [clojurewerkz.elastisch.rest :as esr]))

(def es-address (get (System/getenv) "ES-ADDRESS" "http://127.0.0.1:9200"))
(def es-index (get (System/getenv) "ES-INDEX" "st-observer"))

(defn msg-handler
  "Handler for firehose messages, persists messages into configured
   ElasticSearch index."
  [{:keys [current-state msg-type msg-payload]}]
  (let [conn (:conn current-state)
        ;        es-id (str (st/now))
        ;es-id (str (:cmp-id msg-payload) "-" (:corr-id (:msg-meta msg-payload)))
        es-id (:firehose-id msg-payload)
        new-state (-> current-state
                      (update-in [:count] inc)
                      (update-in [:messages] #(vec (conj % msg-payload))))
        doc {:msg (pr-str msg-payload)}]
    (try
      (esd/put conn es-index "st-msg" es-id doc)
      (catch Exception ex (log/error "Exception when persisting msg:" ex)))
    {:new-state new-state}))

(defn persistence-state-fn
  "Initializes ElasticSearch connection."
  [_put-fn]
  (let [state (atom {:count    0
                     :messages []
                     :conn     (esr/connect es-address)})]
    (log/info "ElasticSearch connection started to" es-address)
    {:state state}))

(defn cmp-map
  [cmp-id]
  {:cmp-id      cmp-id
   :state-fn    persistence-state-fn
   :handler-map {:firehose/cmp-put           msg-handler
                 :firehose/cmp-recv          msg-handler
                 :firehose/cmp-publish-state msg-handler
                 :firehose/cmp-recv-state    msg-handler}})
