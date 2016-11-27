(ns matthiasn.systems-toolbox-observer.menu
  (:require
    [matthiasn.systems-toolbox-ui.reagent :as r]
    [clojure.string :as s]))

(defn menu-view
  "Renders search component."
  [{:keys [put-fn]}]
  (let []
    [:div.search
     [:h1 "systems-toolbox-observer"]
     [:span.btn {:on-click #(put-fn [:grid/add-widget])} "add"]
     [:span.btn {:on-click #(put-fn [:grid/toggle-drag])} "toggle drag"]]))

(defn cmp-map
  [cmp-id]
  (r/cmp-map {:cmp-id  cmp-id
              :view-fn menu-view
              :dom-id  "search"}))
