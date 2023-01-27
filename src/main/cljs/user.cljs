(ns cljs.user
  (:require [clojure.test :refer [run-tests]]
            [rsi.multiplication-tables-test]))

(defn run-all-tests []
  (run-tests 'rsi.multiplication-tables-test))

(comment
  (run-all-tests))
