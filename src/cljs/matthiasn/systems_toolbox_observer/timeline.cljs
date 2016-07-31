(ns matthiasn.systems-toolbox-observer.timeline
  (:require [matthiasn.systems-toolbox-ui.reagent :as r]
            [cljsjs.moment]
            [reagent.core :as rc]
            [cljs.pprint :as pp]))

(defn pprinted-div
  [data label]
  (let [expanded (rc/atom false)
        toggle-click {:on-click #(swap! expanded not)}]
    (fn [data label]
      (if @expanded
        [:div
         [:span.show-data toggle-click (str "hide " label)]
         [:pre [:code (with-out-str (pp/pprint data))]]]
        [:span.show-data toggle-click (str "show " label)]))))

(defn timeline-entry
  ""
  [entry]
  [:div.entry
   [:time (.format (.utc js/moment (:ts entry)) "YYYY-MM-DD HH:MM:ss.SSS")] " "
   (:cmp-id entry) " "
   (when (:msg entry) [pprinted-div (:msg-meta entry) "metadata"])
   (when (:msg entry) [pprinted-div (:msg entry) "msg"])
   (when (:snapshot entry) [pprinted-div (:snapshot entry) "snapshot"])])

(defn timeline-view
  ""
  [{:keys [observed put-fn]}]
  (let [store-snapshot @observed]
    [:div.timeline
     [:div.journal-entries
      (for [entry (take 50 (reverse (:entries store-snapshot)))]
        ^{:key (:firehose-id entry)}
        [timeline-entry entry])]]))

(defn cmp-map
  [cmp-id]
  (r/cmp-map {:cmp-id  cmp-id
              :view-fn timeline-view
              :dom-id  "timeline"}))
