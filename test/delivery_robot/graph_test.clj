(ns delivery-robot.graph-test
  (:require [clojure.test :refer :all]
            [delivery-robot.graph :refer :all]))

(deftest create-node-test
  (testing "should create a node with name"
    (is (= (create-node "room 1") (hash-map :name "room 1", :packages '(), :neighbours '())))))

(deftest add-neighbour-test
  (testing "should add a neighbour to a given node with a cost"
    (let [node (create-node "room 1")
          neighbour (hash-map :name "room 2", :cost 2)]
      (is (=
           (add-neighbour node "room 2" 2)
           (hash-map :name "room 1", :packages '(), :neighbours (list neighbour)))))))

(deftest get-node-test
  (testing "should get the node with name from a graph"
    (let [nodeA (create-node "room 1")
          nodeB (create-node "room 2")
          nodeC (create-node "room 3")
          graph (list nodeA nodeB nodeC)]
      (is (= (get-node "room 2" graph) nodeB)))))
