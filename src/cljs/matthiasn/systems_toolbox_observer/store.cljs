(ns matthiasn.systems-toolbox-observer.store
  (:require [cljs.pprint :as pp]))

(defn msg-handler
  [{:keys [msg-payload]}]
  (pp/pprint msg-payload))

(defn cmp-map
  "Client-side store component map."
  [cmp-id]
  {:cmp-id      cmp-id
   ;   :state-fn    initial-state-fn
   ;   :state-spec        :state/client-store-spec
   :handler-map {:firehose/msg msg-handler}})
