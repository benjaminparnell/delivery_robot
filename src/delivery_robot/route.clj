(ns delivery-robot.route
  (:use [clojure.math.combinatorics :as combo]
        [delivery-robot.graph :as g]))

(defn add
  [node route]
  (let [cost (+ (first route) (node :cost))]
    (into [] (concat [cost] (rest route) [(node :name)]))))

(defn combine
  "combines a list of routes into a single route"
  [coll]
  (map (fn [r]
         (reduce (fn [prev curr]
                   (into [(+ (first prev) (first curr))] (into (vec (rest prev)) (rest (rest curr))))) r)) coll))

(defn perms
  [start dests]
  (let [perms (combo/permutations (distinct (remove #(= start %) dests)))]
    (map #(conj (into [start] (vec %)) "office") perms)))

(defn get-best-perm
  [coll graph]
  (g/get-best-route (combine (map (fn [p]
         (let [del (partition 2 1 p)]
           (map #(g/get-best-route graph (first %) (nth % 1)) del))) coll))))
