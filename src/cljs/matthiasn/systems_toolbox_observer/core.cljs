(ns matthiasn.systems-toolbox-observer.core
  (:require [matthiasn.systems-toolbox.switchboard :as sb]
            [matthiasn.systems-toolbox-sente.client :as sente]
            [matthiasn.systems-toolbox-observer.spec]
            [matthiasn.systems-toolbox-observer.store :as store]
            [matthiasn.systems-toolbox-observer.menu :as menu]
            [matthiasn.systems-toolbox-observer.grid :as g]))

(enable-console-print!)

(defonce switchboard (sb/component :client/switchboard))

(def sente-cfg {:relay-types #{:entries/query :entry/fetch}})

(defn init!
  "Initializes client-side system."
  []
  (sb/send-mult-cmd
    switchboard
    [[:cmd/init-comp #{(sente/cmp-map :client/ws-cmp sente-cfg)
                       (store/cmp-map :client/store-cmp)
                       (menu/cmp-map :client/menu-cmp)
                       (g/cmp-map :client/grid-cmp)}]

     [:cmd/route {:from #{:client/ws-cmp
                          :client/menu-cmp
                          :client/grid-cmp}
                  :to   :client/store-cmp}]

     [:cmd/route {:from #{:client/menu-cmp
                          :client/grid-cmp}
                  :to   :client/ws-cmp}]

     [:cmd/observe-state {:from :client/store-cmp
                          :to   #{:client/grid-cmp}}]]))

(init!)
