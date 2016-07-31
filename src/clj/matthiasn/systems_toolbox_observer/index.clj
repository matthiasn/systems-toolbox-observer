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
      [:title "Inspect"]
      [:link {:href "/webjars/normalize-css/3.0.3/normalize.css"
              :media "screen" :rel "stylesheet"}]
      [:link {:href "/webjars/github-com-mrkelly-lato/0.3.0/css/lato.css"
              :media "screen" :rel "stylesheet"}]
      [:link {:href "/webjars/fontawesome/4.6.3/css/font-awesome.css"
              :media "screen" :rel "stylesheet"}]
      [:link {:href "/webjars/leaflet/0.7.7/dist/leaflet.css"
              :media "screen" :rel "stylesheet"}]
      [:link {:href "/css/iwaswhere.css" :media "screen" :rel "stylesheet"}]]
     [:body
      [:div.flex-container
       [:div#header]
       [:div#search]
       [:div#journal]]
      ;; Currently, from http://www.orangefreesounds.com/old-clock-ringing-short/
      ;; TODO: record own alarm clock
      [:audio#ringer {:autoPlay false :loop false}
       [:source {:src "/mp3/old-clock-ringing-short.mp3" :type "audio/mp4"}]]
      [:audio#ticking-clock {:autoPlay false :loop false}
       [:source {:src "/mp3/tick.ogg" :type "audio/ogg"}]]
      [:script {:src "/js/build/iwaswhere.js"}]]]))

(def sente-map
  "Configuration map for sente-cmp."
  {:index-page-fn index-page
   :relay-types   #{:cmd/keep-alive-res :entry/saved :state/new}})
