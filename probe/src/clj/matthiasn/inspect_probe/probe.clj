(ns matthiasn.inspect-probe.probe
  (:require [matthiasn.systems-toolbox.switchboard :as sb]
            [matthiasn.systems-toolbox-redis.sender :as redis]))

(def redis-host (get (System/getenv) "PROBE_REDIS_HOST" "127.0.0.1"))
(def redis-port (Integer/parseInt
                  (get (System/getenv) "PROBE_REDIS_PORT" "6379")))

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
                      :host        redis-host
                      :port        redis-port
                      :topic       "firehose"})]
     [:cmd/attach-to-firehose :server/redis-cmp]
     [:cmd/route {:from :server/ws-cmp
                  :to   :server/redis-cmp}]]))
