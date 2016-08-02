(ns matthiasn.systems-toolbox-observer.spec
  (:require #?(:clj  [clojure.spec :as s]
               :cljs [cljs.spec :as s])))

(s/def :firehose/msg
  (s/keys :req-un [:iww.cfg/path
                   :iww.entry/timestamp]))

(s/def :qs/default_operator string?)
(s/def :qs/query string?)

(s/def :q/query_string
  (s/keys :req-un [:qs/default_operator
                   :qs/query]))

(s/def :q/query
  (s/keys :req-un [:q/query_string]))

(s/def :q/n pos-int?)
(s/def :q/from int?)

(s/def :entries/search
  (s/keys :req-un [:q/query
                   :q/n
                   :q/from]))


