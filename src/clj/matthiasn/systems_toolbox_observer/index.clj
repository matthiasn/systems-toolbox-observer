(ns matthiasn.systems-toolbox-observer.index
  "This namespace takes care of rendering the static HTML into which the
   React / Reagent components are mounted on the client side at runtime."
  (:require [hiccup.core :refer [html]]))

(defn index-page
  "Generates index page HTML with the specified page title."
  [_]
  (html
    [:html
     {:lang "en"}
     [:head
      [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
      [:title "systems-toolbox-observer"]
      [:link {:href "/webjars/normalize-css/4.1.1/normalize.css"
              :media "screen" :rel "stylesheet"}]
      [:link {:href "/webjars/github-com-mrkelly-lato/0.3.0/css/lato.css"
              :media "screen" :rel "stylesheet"}]
      [:link {:href "/webjars/fontawesome/4.6.3/css/font-awesome.css"
              :media "screen" :rel "stylesheet"}]
      [:link {:href "/css/observer.css" :media "screen" :rel "stylesheet"}]]
     [:body
      [:div.flex-container
       [:div#header]
       [:div#search]
       [:div#grid]]
      [:script {:src "/js/build/observer.js"}]]]))

(def sente-map
  "Configuration map for sente-cmp."
  {:index-page-fn index-page
   :relay-types   #{:firehose/msg :firehose/snapshot :entries/prev}})
