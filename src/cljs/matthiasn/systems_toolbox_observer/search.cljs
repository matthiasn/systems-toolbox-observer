(ns matthiasn.systems-toolbox-observer.search
  (:require
    [matthiasn.systems-toolbox-ui.reagent :as r]
    [clojure.string :as s]))

(defn search-view
  "Renders search component."
  [{:keys [put-fn]}]
  (let [on-input-fn
        (fn [ev]
          (let [query {:query_string
                       {:default_operator "AND"
                        :query            (.. ev -target -innerText)}}]
            (put-fn [:entries/query {:query query
                                     :n     25
                                     :from  0}])))]
    [:div.search
     [:h1 "Inspect v2"]
     [:div.search-field {:content-editable true
                         :on-input         on-input-fn}]]))

(defn cmp-map
  [cmp-id]
  (r/cmp-map {:cmp-id  cmp-id
              :view-fn search-view
              :dom-id  "search"}))
