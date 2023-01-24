(ns rsi.multiplication-tables-test
  (:require [cljs.test :refer (deftest is testing)]
            [rsi.multiplication-tables :refer [update-highscore update-score]]))

(deftest score
  (testing "increments given correct answer in time"
    (is (=
         {:deadline-passed? false
          :score 1}
         (update-score {:deadline-passed? false
                        :score 0} true))))
  (testing "does not increment given late correct answer"
    (is (=
         {:deadline-passed? true
          :score 0}
         (update-score {:deadline-passed? true
                        :score 0} true))))
  (testing "does not increment given wrong answer"
    (is (=
         {:score 0}
         (update-score {:score 0} false)))))

(deftest highscore
  (is (=
       {:score 3
        :highscore 3}
       (update-highscore {:score 3
                          :highscore 2}))))
