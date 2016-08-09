(ns matthiasn.systems-toolbox-observer.spec
  (:require #?(:clj  [clojure.spec :as s]
               :cljs [cljs.spec :as s])
    [matthiasn.systems-toolbox.spec]))

(s/def :firehose/msg :firehose/cmp-recv)
(s/def :firehose/snapshot :firehose/cmp-publish-state)

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

(s/def :q/result vector?)

(s/def :entries/prev
  (s/keys :req-un [:q/result]))
