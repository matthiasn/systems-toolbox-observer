(ns matthiasn.systems-toolbox-observer.grid
  (:require [matthiasn.systems-toolbox-ui.reagent :as r]
            [cljsjs.moment]
            [cljsjs.react-grid-layout]
            [reagent.core :as rc]
            [cljs.pprint :as pp]))

(defn pprinted-div
  "Show pretty printed data structure. When not fetched yet, initiate
   fetch from backend."
  [entry k state snapshot put-fn]
  (when (and (k @state) (k entry))
    (let [firehose-id (:firehose-id entry)]
      (prn firehose-id (get-in snapshot [:fetched firehose-id]))
      (if (or (= :not-fetched (:snapshot entry))
              (= :not-fetched (second (:msg entry))))
        (if-let [fetched (get-in snapshot [:fetched firehose-id])]
          [:pre [:code (with-out-str (pp/pprint (k fetched)))]]
          (put-fn [:entry/fetch firehose-id]))
        [:pre [:code (with-out-str (pp/pprint (k entry)))]]))))

(defn toggle-button
  ""
  [entry state k label]
  (when (k entry)
    [:span.show-btn {:on-click (fn [_] (swap! state update-in [k] not))}
     (str (if (k @state) "hide " "show ") label)]))

(defn timeline-entry
  ""
  [entry snapshot put-fn]
  (let [state (rc/atom {:msg      false
                        :msg-meta false
                        :snapshot false})]
    (fn [entry snapshot put-fn]
      [:div.entry
       [:div
        [:time (.format (.utc js/moment (:ts entry)) "YYYY-MM-DD HH:mm:ss.SSS")]
        (str (:cmp-id entry) " ")
        (str (first (:msg entry)))]
       [:div (:firehose-id entry)]
       [:div.show-data-btns
        [toggle-button entry state :msg-meta "metadata"]
        [toggle-button entry state :msg "msg"]
        [toggle-button entry state :snapshot "snapshot"]]
       [:div
        [pprinted-div entry :msg-meta state snapshot put-fn]
        [pprinted-div entry :msg state snapshot put-fn]
        [pprinted-div entry :snapshot state snapshot put-fn]]])))

(defn search-view
  "Renders search component."
  [put-fn qid]
  (let [on-input-fn
        (fn [ev]
          (let [query {:query_string
                       {:default_operator "AND"
                        :query            (.. ev -target -innerText)}}]
            (put-fn [:entries/query {:query    query
                                     :n        25
                                     :query-id qid
                                     :from     0}])))]
    [:div.search
     [:div.search-field {:content-editable true
                         :on-input         on-input-fn}]]))

(defn new-entries
  [query-id cnt put-fn]
  (when (pos? cnt)
    (let [show-new (fn [n]
                     (put-fn [:cmd/show-new {:query-id query-id :n n}]))]
      [:div
       [:span.btn {:on-click (fn [_] (show-new cnt))}
        "show " cnt " new matches"]
       [:span.btn {:on-click (fn [_] (show-new 1))}
        "show next match"]])))

(defn timeline-view
  ""
  [snapshot put-fn query-id]
  (let [new-cnt (count (query-id (:new-entries snapshot)))
        entries (take 250 (reverse (query-id (:entries snapshot))))]
    [:div.timeline
     [search-view put-fn query-id]
     [new-entries query-id new-cnt put-fn]
     (for [entry entries]
       ^{:key (:firehose-id entry)}
       [timeline-entry entry snapshot put-fn])]))

(defn widget-view
  [snapshot query-id put-fn cfg]
  [:div.widget {:key       query-id
                :data-grid (:data-grid cfg)}
   [timeline-view snapshot put-fn query-id]])

(def react-grid-layout (rc/adapt-react-class js/ReactGridLayout))

(defn grid-view
  "Renders grid view."
  [{:keys [observed put-fn] :as cmp-map}]
  (let [snapshot @observed
        local-cfg {}
        cfg (:cfg snapshot)
        configurable? (:reconfigure-grid cfg)
        widgets (:widgets snapshot)]
    (when (seq widgets)
      [:div.grid-view
       (into
         [react-grid-layout
          {:width            (.-innerWidth js/window)
           :row-height       20
           :cols             24
           :margin           [8 8]
           :is-draggable     configurable?
           :is-resizable     configurable?
           :class            "tile-journal"
           :on-layout-change (fn [layout]
                               (pp/pprint
                                 (js->clj layout :keywordize-keys true)))}]
         (mapv (fn [[k v]] (widget-view snapshot k put-fn v)) widgets))])))

(defn cmp-map
  [cmp-id]
  (r/cmp-map {:cmp-id  cmp-id
              :view-fn grid-view
              :dom-id  "grid"}))
