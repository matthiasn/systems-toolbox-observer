(ns matthiasn.systems-toolbox-observer.timeline
  (:require [matthiasn.systems-toolbox-ui.reagent :as r]
            [cljsjs.moment]
            [reagent.core :as rc]
            [cljs.pprint :as pp]))

(defn pprinted-div
  ""
  [entry k state]
  (when (and (k @state) k entry)
    [:pre [:code (with-out-str (pp/pprint (k entry)))]]))

(defn toggle-button
  ""
  [entry state k label]
  (when (k entry)
    [:span.show-data {:on-click (fn [_] (swap! state update-in [k] not))}
     (str (if (k @state) "hide " "show ") label)]))

(defn timeline-entry
  ""
  [entry]
  (let [state (rc/atom {:msg      false
                        :msg-meta false
                        :snapshot false})]
    (fn [entry]
      [:div.entry
       [:time (.format (.utc js/moment (:ts entry)) "YYYY-MM-DD HH:mm:ss.SSS")]
       (str (:cmp-id entry) " ")
       (str (first (:msg entry)))
       [toggle-button entry state :msg-meta "metadata"]
       [toggle-button entry state :msg "msg"]
       [toggle-button entry state :snapshot "snapshot"]
       [pprinted-div entry :msg-meta state]
       [pprinted-div entry :msg state]
       [pprinted-div entry :snapshot state]])))

(defn timeline-view
  ""
  [{:keys [observed put-fn]}]
  (let [store-snapshot @observed]
    [:div.timeline
     [:div.journal-entries
      (for [entry (take 250 (reverse (:entries store-snapshot)))]
        ^{:key (:firehose-id entry)}
        [timeline-entry entry])]]))

(defn cmp-map
  [cmp-id]
  (r/cmp-map {:cmp-id  cmp-id
              :view-fn timeline-view
              :dom-id  "timeline"}))
