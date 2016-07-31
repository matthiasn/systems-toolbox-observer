(ns matthiasn.systems-toolbox-observer.store
  (:require [cljs.pprint :as pp]))

(defn msg-handler
  ""
  [{:keys [current-state msg-payload]}]
  {:new-state (update-in current-state [:entries] conj msg-payload)})

(defn state-fn
  ""
  [_put-fn]
  (let [state (atom {:entries []})]
    {:state state}))

(defn cmp-map
  "Client-side store component map."
  [cmp-id]
  {:cmp-id      cmp-id
   :state-fn    state-fn
   ;   :state-spec        :state/client-store-spec
   :handler-map {:firehose/msg msg-handler}})
