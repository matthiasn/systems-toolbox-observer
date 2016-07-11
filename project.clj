(defproject matthiasn/systems-toolbox-observer "0.6.1-alpha1"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha8"]
                 [org.clojure/clojurescript "1.9.93"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [ch.qos.logback/logback-classic "1.1.7"]
                 [hiccup "1.0.5"]
                 [hiccup-bridge "1.0.1"]
                 [clj-pid "0.1.2"]
                 [matthiasn/systems-toolbox "0.6.1-alpha1"]
                 [matthiasn/systems-toolbox-ui "0.6.1-alpha5"]
                 [matthiasn/systems-toolbox-sente "0.6.1-alpha3"]
                 [matthiasn/systems-toolbox-metrics "0.6.1-alpha1"]
                 [matthiasn/systems-toolbox-redis "0.6.1-alpha1"]
                 [incanter "1.5.6"]
                 [clj-time "0.12.0"]]
  :main matthiasn.systems-toolbox-observer.core)
