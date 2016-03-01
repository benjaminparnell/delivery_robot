(ns delivery-robot.core
  (:use [delivery-robot.robot :as robot]
        [delivery-robot.route :as route]
        [delivery-robot.graph :as g]
        [clojure.math.combinatorics :as combo]))

(def assignment-graph (g/load-from-file "graph.txt"))
(def test-graph (g/load-from-file "test-graph.txt"))

(defn get-route-perms
  [start dests]
  (let [perms (combo/permutations (remove #(= start %) dests))]
    (map #(conj (into [start] (vec %)) "office") perms)))

(let [perms (get-route-perms "office" ["storage" "d1" "r111"])]
  (g/get-best-route (combine (map (fn [p]
         (let [del (partition 2 1 p)]
           (map #(g/get-best-route assignment-graph (first %) (nth % 1)) del))) perms))))

(defn combine
  "combines a list of routes into a single route"
  [coll]
  (map (fn [r]
         (reduce (fn [prev curr]
                   (into [(+ (first prev) (first curr))] (into (vec (rest prev)) (rest (rest curr))))) r)) coll))
