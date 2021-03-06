(defproject matthiasn/systems-toolbox-observer "0.6.1-alpha3"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/clojurescript "1.9.293"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [ch.qos.logback/logback-classic "1.1.7"]
                 [hiccup "1.0.5"]
                 [hiccup-bridge "1.0.1"]
                 [clj-pid "0.1.2"]
                 [pandect "0.6.1"]
                 [cljsjs/moment "2.10.6-4"]
                 [com.taoensso/encore "2.87.0"]
                 [matthiasn/systems-toolbox "0.6.2"]
                 [matthiasn/systems-toolbox-redis "0.6.2"]
                 [matthiasn/systems-toolbox-ui "0.6.1"]
                 [matthiasn/systems-toolbox-sente "0.6.1"]
                 [matthiasn/systems-toolbox-metrics "0.6.1"]
                 [cljsjs/react-grid-layout "0.13.7-0"]
                 [clojurewerkz/elastisch "2.2.2"]
                 [org.clojure/data.avl "0.0.17"]
                 [org.webjars.bower/fontawesome "4.6.3"]
                 [org.webjars.bower/normalize-css "4.1.1"]
                 [org.webjars.npm/github-com-mrkelly-lato "0.3.0"]
                 [incanter "1.5.6"]
                 [clj-time "0.12.2"]]

  :source-paths ["src/cljc/" "src/clj/"]

  :main matthiasn.systems-toolbox-observer.core

  :plugins
  [[lein-cljsbuild "1.1.4" :exclusions [org.apache.commons/commons-compress]]
   [lein-figwheel "0.5.8" :exclusions [org.clojure/clojure]]
   [lein-sassy "1.0.8"
    :exclusions [org.clojure/clojure org.codehaus.plexus/plexus-utils]]
   [com.jakemccrary/lein-test-refresh "0.18.0"]
   [test2junit "1.2.5"]
   [lein-doo "0.1.7"]
   [lein-codox "0.10.2" :exclusions [org.clojure/clojure]]]

  :sass {:src "src/scss/"
         :dst "resources/public/css/"}

  :figwheel {:server-port 3448
             :css-dirs    ["resources/public/css"]}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljc" "src/cljs" "env/dev/cljs"]
     :figwheel     true
     :compiler     {:main          "matthiasn.systems-toolbox-observer.dev"
                    :asset-path    "js/build"
                    :optimizations :none
                    :output-dir    "resources/public/js/build/"
                    :output-to     "resources/public/js/build/observer.js"
                    :source-map    true}}
    {:id           "release"
     :source-paths ["src/cljc" "src/cljs"]
     :compiler     {:main          "matthiasn.systems-toolbox-observer.core"
                    :asset-path    "js/build"
                    :output-to     "resources/public/js/build/observer.js"
                    :optimizations :advanced}}]})
