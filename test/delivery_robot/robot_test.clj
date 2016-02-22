(ns delivery-robot.robot-test
  (:require [clojure.test :refer :all]
            [delivery-robot.robot :refer :all]))

(deftest spawn-test
  (testing "with no arguments"
    (is (= (spawn) (hash-map :position "", :packages '())))))

(deftest position-test
  (testing "no arguments"
    (let [robot (spawn)]
      (is (= (position robot) "")))))

(deftest move-test
  (testing "should move robot to new position"
    (let [robot (spawn)]
      (is (= (position (move robot "1")) "1")))))

(deftest packages-test
  (testing "should return the current list of packages"
    (let [robot (spawn)]
      (is (= (packages robot) '())))))

(deftest add-package-test
  (testing "should add a package to the robots packages"
    (let [robot (spawn)]
      (is (= (packages (add-package robot "1")) '("1"))))))
