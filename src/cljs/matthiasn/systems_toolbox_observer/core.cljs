(ns matthiasn.systems-toolbox-observer.core
  (:require [matthiasn.systems-toolbox.switchboard :as sb]
            [matthiasn.systems-toolbox-sente.client :as sente]
            [matthiasn.systems-toolbox-observer.store :as store]
            [matthiasn.systems-toolbox-observer.timeline :as tl]))

(enable-console-print!)

(defonce switchboard (sb/component :client/switchboard))

(def sente-cfg {:relay-types #{:firehose/msg}})

(defn init!
  "Initializes client-side system."
  []
  (sb/send-mult-cmd
    switchboard
    [[:cmd/init-comp #{(sente/cmp-map :client/ws-cmp sente-cfg)
                       (store/cmp-map :client/store-cmp)
                       (tl/cmp-map :client/timeline-cmp)}]

     [:cmd/route {:from #{:client/ws-cmp}
                  :to   :client/store-cmp}]

     [:cmd/observe-state {:from :client/store-cmp
                          :to   #{:client/timeline-cmp}}]]))

(init!)

