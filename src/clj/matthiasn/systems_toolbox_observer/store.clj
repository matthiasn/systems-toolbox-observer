(ns matthiasn.systems-toolbox-observer.store
  "Store component for doing meaningful stuff with firehose messages."
  (:require [clojure.pprint :as pp]
            [clojurewerkz.elastisch.rest.document :as esd]
            [clojurewerkz.elastisch.rest.response :as esrsp]
            [clojurewerkz.elastisch.rest :as esr]
            [matthiasn.systems-toolbox.component :as st]
            [clojure.tools.logging :as log]))

(def es-address (get (System/getenv) "ES-ADDRESS" "http://127.0.0.1:9200"))
(def es-index (get (System/getenv) "ES-INDEX" "st-observer"))

(defn firehose-msg-handler
  "Handler for firehose messages, persists messages into configured
   ElasticSearch index."
  [{:keys [current-state msg-type msg-payload]}]
  (let [conn (:conn current-state)
        es-id (:firehose-id msg-payload)
        new-state (-> current-state
                      (update-in [:count] inc)
                      (update-in [:messages] #(vec (conj % msg-payload))))
        edn-msg (pr-str msg-payload)
        doc {:msg edn-msg
             :ts  (:ts msg-payload)}]
    (try
      (esd/put conn es-index "st-msg" es-id doc)
      (catch Exception ex (log/error "Exception when persisting msg:" ex)))
    {:new-state new-state
     :emit-msg  (with-meta [:firehose/msg (read-string edn-msg)]
                           {:sente-uid :broadcast})}))

(defn es-query
  "Handler function for previous entries. Emits message with results of
   ElasticSearch query."
  [{:keys [current-state msg-payload]}]
  (prn "es-query" msg-payload)
  (let [conn (:conn current-state)
        {:keys [query n from]} msg-payload
        search (esd/search conn es-index "st-msg"
                           :query query
                           :size n
                           :from from
                           :sort {:ts :desc})
        hits (esrsp/hits-from search)
        res (->> hits
                 (map :_source)
                 (map :msg)
                 (map read-string))]
    {:emit-msg [:entries/prev {:result (vec (reverse res))}]}))

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
   :handler-map {:firehose/cmp-put           firehose-msg-handler
                 :firehose/cmp-recv          firehose-msg-handler
                 :firehose/cmp-publish-state firehose-msg-handler
                 :firehose/cmp-recv-state    firehose-msg-handler
                 :entries/query              es-query}})
