(ns matthiasn.systems-toolbox-observer.store
  "Store component for doing meaningful stuff with firehose messages."
  (:require [clojurewerkz.elastisch.rest.document :as esd]
            [clojurewerkz.elastisch.rest.response :as esrsp]
            [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.percolation :as perc]
            [pandect.core :as p]
            [clojure.tools.logging :as log]
            [matthiasn.systems-toolbox.handler-utils :as hu]
            [clojure.pprint :as pp]))

(def es-address (get (System/getenv) "ES-ADDRESS" "http://127.0.0.1:9200"))
(def es-index (get (System/getenv) "ES-INDEX" "st-observer"))
(def es-perc-index (get (System/getenv) "ES-PERC-INDEX" "st-observer-perc"))
(def es-msg-type "st-msg")

(defn deliver-perc
  "Find and deliver pecolation matches."
  [current-state conn put-fn msg-type msg-payload doc]
  (let [response (perc/percolate conn es-perc-index es-msg-type :doc doc)
        matches (set (map :_id (esrsp/matches-from response)))
        connected-uids (:connected-uids current-state)
        subscriptions (:subscriptions current-state)
        put-msg-type (if (msg-type #{:firehose/cmp-recv :firehose/cmp-put})
                       :firehose/msg
                       :firehose/snapshot)]
    (doseq [uid (:any connected-uids)]
      (when (contains? matches (get subscriptions uid))
        (put-fn (with-meta [put-msg-type msg-payload] {:sente-uid uid}))))))

(defn firehose-msg-handler
  "Handler for firehose messages, persists messages into configured
   ElasticSearch index."
  [{:keys [current-state msg-type msg-payload put-fn]}]
  (let [conn (:conn current-state)
        es-id (:firehose-id msg-payload)
        new-state (-> current-state
                      (update-in [:count] inc)
                      (update-in [:messages] #(vec (conj % msg-payload))))
        doc {:msg (pr-str msg-payload) :ts (:ts msg-payload)}]
    (log/info "Firehose message" msg-type msg-payload)
    (try
      (esd/put conn es-index es-msg-type es-id doc)
      (deliver-perc current-state conn put-fn msg-type msg-payload doc)
      (catch Exception ex (log/error "Exception when persisting msg:" ex)))
    {:new-state new-state}))

(defn es-query
  "Handler function for previous entries. Emits message with results of
   ElasticSearch query.
   Also registers percolation search with ID based on hash of the query."
  [{:keys [current-state msg-payload msg-meta]}]
  (prn "es-query" msg-payload)
  (let [conn (:conn current-state)
        {:keys [query n from]} msg-payload
        sha (p/sha1 (str query))
        uid (:sente-uid msg-meta)
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
    (perc/register-query conn es-perc-index sha :query query)
    {:new-state (assoc-in current-state [:subscriptions uid] sha)
     :emit-msg [:entries/prev {:result (vec (reverse res))}]}))

(defn persistence-state-fn
  "Initializes ElasticSearch connection."
  [_put-fn]
  (let [state (atom {:count          0
                     :messages       []
                     :conn           (esr/connect es-address)
                     :subscriptions  {}
                     :connected-uids {}})]
    (log/info "ElasticSearch connection started to" es-address)
    {:state state}))

(defn cmp-map
  [cmp-id]
  {:cmp-id            cmp-id
   :state-fn          persistence-state-fn
   :handler-map       {:firehose/cmp-put           firehose-msg-handler
                       :firehose/cmp-recv          firehose-msg-handler
                       :firehose/cmp-publish-state firehose-msg-handler
                       :firehose/cmp-recv-state    firehose-msg-handler
                       :entries/query              es-query}
   :state-pub-handler (hu/assoc-in-cmp [:connected-uids])})
