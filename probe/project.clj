(defproject matthiasn/inspect-probe "0.6.1-alpha1"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[matthiasn/systems-toolbox-redis "0.6.1-alpha3"]]

  :profiles {:dev
             {:dependencies
              [[org.clojure/clojure "1.9.0-alpha10"]
               [matthiasn/systems-toolbox "0.6.1-alpha4"]]}}

  :source-paths ["src/cljc/" "src/clj/"])
