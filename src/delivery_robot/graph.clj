(ns delivery-robot.graph
  (:require [delivery-robot.util :refer :all]
            [clojure.math.combinatorics :as combo]))

(defn create-node
  "create-node returns a hash map of a node name with no packages in it and
   no neighbours"
  [name]
  (hash-map :name name, :packages '(), :neighbours '()))

(defn add-neighbour
  "add-neighbour adds a neighbour to node with a cost of moving between
   node and neighbour of cost"
  [node neighbour cost]
  (update-in
    node
    [:neighbours]
    conj
    (hash-map :name neighbour, :cost cost)))

(defn get-node
  "get-node gets the node with name from graph"
  [name graph]
  (first
    (filter #(= (%1 :name) name) graph)))

(defn not-visited
  "gets all the neighbours that aren't visited in a route"
  [neighbours route]
  (filter #(not (in? route (get %1 :name))) neighbours))

(defn route-add
  [node route]
  (let [cost (+ (first route) (node :cost))]
    (into [] (concat [cost] (rest route) [(node :name)]))))

(defn get-routes
  ([graph a b] (get-routes graph (get-node a graph) [0 a] (not-visited (get (get-node a graph) :neighbours) [0 a]) b))
  ([graph curr route next-moves dest]
   (if (= (get curr :name) dest)
    route
    (flat
      (map #(get-routes
              graph
              (get-node (get %1 :name) graph)
              (route-add %1 route)
              (not-visited (get (get-node (% :name) graph) :neighbours) (route-add %1 route))
              dest) next-moves)))))

(defn get-best-route
  ([routes] (first (sort-by first routes)))
  ([graph a b] (first (sort-by first (get-routes graph a b)))))

(defn combine
  "combines a list of routes into a single route"
  [coll]
  (map (fn [r]
         (reduce (fn [prev curr]
                   (into [(+ (first prev) (first curr))] (into (vec (rest prev)) (rest (rest curr))))) r)) coll))

(defn perms
  [start dests]
  (let [perms (combo/permutations (distinct (remove #(or (= start %) (= "office" %)) dests)))]
    (map #(conj (into [start] (vec %)) "office") perms)))

(defn get-best-perm
  [coll graph]
  (get-best-route (combine (map (fn [p]
    (let [del (partition 2 1 p)]
     (map #(get-best-route graph (first %) (nth % 1)) del))) coll))))

