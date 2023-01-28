(ns cljs.user
  (:require [cljs.test]
            [rsi.multiplication-tables-test]))

(comment
  (cljs.test/run-all-tests)
  (cljs.test/run-all-tests #"rsi.*-test"))
