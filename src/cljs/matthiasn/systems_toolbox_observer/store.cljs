(ns matthiasn.systems-toolbox-observer.store
  (:require [cljs.pprint :as pp]
            [matthiasn.systems-toolbox.component.helpers :as h]))

(defn msg-handler
  "Adds received message to entries in component state."
  [{:keys [current-state msg-payload msg-meta]}]
  (let [qid (:query-id msg-meta)]
    {:new-state (update-in current-state [:new-entries qid] conj msg-payload)}))

(defn state-fn
  "Generate initial component state."
  [_put-fn]
  (let [state (atom {:entries {}
                     :cfg (:reconfigure-grid false)
                     :widgets {:query-1 {:type      :timeline
                                         :data-grid {:x 0 :y 0 :w 8 :h 10}}
                               :query-2 {:type      :timeline
                                         :data-grid {:x 8 :y 0 :w 8 :h 10}}
                               :query-3 {:type      :timeline
                                         :data-grid {:x 16 :y 0 :w 8 :h 10}}}})]
    {:state state}))

(defn prev-handler
  "Replace entries in component state with query result."
  [{:keys [current-state msg-payload]}]
  (let [qid (:query-id msg-payload)
        new-state (-> current-state
                      (assoc-in [:entries qid] (:result msg-payload))
                      (assoc-in [:new-entries qid] ()))]
    {:new-state new-state}))

(defn show-new-handler
  "Replace entries in component state with query result."
  [{:keys [current-state msg-payload]}]
  (let [{:keys [query-id n]} msg-payload
        new-entries (query-id (:new-entries current-state))
        entries (take-last n new-entries)
        new-state (-> current-state
                      (update-in [:entries query-id] concat entries)
                      (update-in [:new-entries query-id] #(drop-last n %)))]
    {:new-state new-state}))

(defn add-widget
  [{:keys [current-state]}]
  (let [qid (keyword (h/make-uuid))
        cfg {:type      :timeline
             :data-grid {:x 16 :y 0 :w 8 :h 10}}]
    {:new-state (assoc-in current-state [:widgets qid] cfg)}))

(defn toggle-drag
  [{:keys [current-state]}]
  {:new-state (update-in current-state [:cfg :reconfigure-grid] not)})

(defn cmp-map
  "Client-side store component map."
  [cmp-id]
  {:cmp-id      cmp-id
   :state-fn    state-fn
   ;   :state-spec        :state/client-store-spec
   :handler-map {:firehose/msg      msg-handler
                 :firehose/snapshot msg-handler
                 :cmd/show-new      show-new-handler
                 :grid/add-widget   add-widget
                 :grid/toggle-drag  toggle-drag
                 :entries/prev      prev-handler}})
