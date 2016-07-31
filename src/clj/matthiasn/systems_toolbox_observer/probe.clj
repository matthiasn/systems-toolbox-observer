(ns matthiasn.systems-toolbox-observer.probe
  (:require [matthiasn.systems-toolbox.switchboard :as sb]
            [matthiasn.systems-toolbox-redis.sender :as redis]))

(defn start!
  "Starts Redis sender component and attaches it to firehose."
  [switchboard]
  (sb/send-mult-cmd
    switchboard
    [[:cmd/init-comp
      (redis/cmp-map :server/redis-cmp
                     {:relay-types #{:firehose/cmp-put
                                     :firehose/cmp-recv
                                     :firehose/cmp-publish-state
                                     :firehose/cmp-recv-state}
                      :host        "127.0.0.1"
                      :port        6379
                      :topic       "firehose"})]
     [:cmd/attach-to-firehose :server/redis-cmp]
     [:cmd/route {:from :server/ws-cmp :to :server/redis-cmp}]]))
