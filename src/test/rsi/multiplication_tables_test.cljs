(ns rsi.multiplication-tables-test
  (:require [cljs.test :refer (deftest is testing)]
            [rsi.multiplication-tables :refer [highscore process-answer score]]))

(deftest increments-score
  (testing "increments given correct answer in time"
    (is (= 1
           (score {:deadline-passed? false
                   :score 0} true))))
  (testing "does not increment given late correct answer"
    (is (= 0
         (score {:deadline-passed? true
                 :score 0} true))))
  (testing "does not increment given wrong answer"
    (is (= 0
         (score {:score 0} false)))))

(deftest increases-highscore
  (is (= 3
         (highscore {:highscore 2} 3))))

(deftest transforming-state
  (testing "correct answer"
    (testing "on time"
      (is (= {:question [1 2]
              :score 1
              :highscore 1
              :mode :against-the-clock
              :wrongly-answered #{}
              :deadline-passed? false}
             (process-answer {:question [2 3]
                              :wrongly-answered #{}} "6" [1 2]))))
    (testing "too late"
      (is (= {:question [1 2]
              :score 0
              :highscore 1
              :mode :against-the-clock
              :wrongly-answered #{[2 3]}
              :deadline-passed? false}
             (process-answer {:question [2 3]
                              :highscore 1
                              :wrongly-answered #{}
                              :deadline-passed? true} "6" [1 2])))))
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
