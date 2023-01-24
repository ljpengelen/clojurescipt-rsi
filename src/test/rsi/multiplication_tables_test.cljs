(ns rsi.multiplication-tables-test
  (:require [cljs.test :refer (deftest is testing)]
            [rsi.multiplication-tables :refer [process-answer update-highscore
                                               update-score]]))

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

(deftest transforming-state
  (testing "correct answer"
    (is (= {:question [1 2]
            :score 1
            :highscore 1
            :mode :against-the-clock
            :deadline-passed? false}
           (process-answer {:question [2 3]} "6" [1 2]))))
   (testing "wrong answer"
     (is (= {:question [2 3]
             :score 0
             :highscore 10
             :mode :correct-current-question
             :wrongly-answered #{[2 3]}
             :deadline-passed? false}
            (process-answer {:question [2 3]
                             :score 5
                             :highscore 10
                             :wrongly-answered #{}} "5" [1 2])))))
