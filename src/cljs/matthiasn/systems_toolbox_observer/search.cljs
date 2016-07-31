(ns matthiasn.systems-toolbox-observer.search
  (:require
    [matthiasn.systems-toolbox-ui.reagent :as r]
            [clojure.string :as s]))

(defn search-view
  "Renders search component."
  [{:keys [observed local put-fn]}]
  (let [local-snapshot @local
        store-snapshot @observed
        on-input-fn #(put-fn [:search/update (.. % -target -innerText)])]
    [:div.search
     [:h1 "Inspect v2"]
     [:div.search-field {:content-editable true
                         :on-input         on-input-fn}
      (:search-text (:current-query local-snapshot))]]))

(defn cmp-map
  [cmp-id]
  (r/cmp-map {:cmp-id  cmp-id
              :view-fn search-view
              :dom-id  "search"}))
