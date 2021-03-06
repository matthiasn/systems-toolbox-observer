(ns matthiasn.systems-toolbox-observer.core
  (:require [matthiasn.systems-toolbox.switchboard :as sb]
            [matthiasn.systems-toolbox-redis.receiver :as redis]
            [matthiasn.systems-toolbox-sente.server :as sente]
            [clojure.tools.logging :as log]
            [clj-pid.core :as pid]
            [matthiasn.systems-toolbox-observer.spec]
            [matthiasn.systems-toolbox-observer.index :as idx]
            [matthiasn.systems-toolbox-observer.store :as st]))

(defonce switchboard (sb/component :server/switchboard))


(defn restart!
  "Starts or restarts system by asking switchboard to fire up the store
   component, Redis client, ElasticSearch client, then wiring all messages from
   Redis to store.."
  []
  (sb/send-mult-cmd
    switchboard
    [[:cmd/init-comp #{(st/cmp-map :server/store-cmp)
                       (sente/cmp-map :server/ws-cmp idx/sente-map)
                       (redis/cmp-map :server/redis-cmp
                                      {:host  "127.0.0.1"
                                       :port  6379
                                       :topic "firehose"})}]
     [:cmd/route {:from :server/redis-cmp :to :server/store-cmp}]
     [:cmd/route {:from :server/ws-cmp :to :server/store-cmp}]
     [:cmd/route {:from :server/store-cmp :to :server/ws-cmp}]
     [:cmd/observe-state {:from :server/ws-cmp :to :server/store-cmp}]]))

(defn -main
  "Starts the application from command line, saves and logs process ID.
   The system that is fired up when restart! is called proceeds in core.async's
   thread pool. Since we don't want the application to exit when just because
   the current thread is out of work, we just put it to sleep."
  [& args]
  (pid/save "observer.pid")
  (pid/delete-on-shutdown! "observer.pid")
  (log/info "Observer application started, PID" (pid/current))
  (restart!)
  (Thread/sleep Long/MAX_VALUE))
