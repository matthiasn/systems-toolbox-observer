(ns matthiasn.systems-toolbox-observer.store
  "Store component for doing meaningful stuff with firehose messages."
  (:require [clojure.pprint :as pp]))

(defn msg-handler
  "Handler for firehose messages"
  [{:keys [current-state msg-type msg-payload]}]
  (let [new-state (-> current-state
                      (update-in [:count] inc)
                      (update-in [:messages] #(vec (conj % msg-payload))))]
    (prn msg-type (:cmp-id msg-payload) (:count new-state))
    ;(pp/pprint msg-payload)
    {:new-state new-state}))


(defn cmp-map
  [cmp-id]
  {:cmp-id      cmp-id
   :state-fn    (fn [_] {:state (atom {:count 0 :messages []})})
   :handler-map {:firehose/cmp-put msg-handler
                 :firehose/cmp-recv msg-handler
                 :firehose/cmp-publish-state msg-handler
                 :firehose/cmp-recv-state msg-handler}})
