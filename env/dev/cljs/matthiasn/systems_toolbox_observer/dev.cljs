(ns ^:figwheel-no-load matthiasn.systems-toolbox-observer.dev
  (:require [matthiasn.systems-toolbox-observer.core :as c]
            [figwheel.client :as figwheel :include-macros true]))

(enable-console-print!)

(defn jscb [] 
  (c/init!))

(figwheel/watch-and-reload
  :websocket-url "ws://localhost:3448/figwheel-ws"
  :jsload-callback jscb)
