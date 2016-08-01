(ns matthiasn.systems-toolbox-observer.store
  (:require [cljs.pprint :as pp]))

(defn msg-handler
  "Adds received message to entries in component state."
  [{:keys [current-state msg-payload]}]
  {:new-state (update-in current-state [:entries] conj msg-payload)})

(defn state-fn
  "Generate initial component state."
  [_put-fn]
  (let [state (atom {:entries []})]
    {:state state}))

(defn prev-handler
  "Replace entries in component state with query result."
  [{:keys [current-state msg-payload]}]
  {:new-state (assoc-in current-state [:entries]
                        (:result msg-payload))})

(defn cmp-map
  "Client-side store component map."
  [cmp-id]
  {:cmp-id      cmp-id
   :state-fn    state-fn
   ;   :state-spec        :state/client-store-spec
   :handler-map {:firehose/msg msg-handler
                 :entries/prev prev-handler}})
