(ns delivery-robot.core
  (:use [delivery-robot.robot :as robot]
        [delivery-robot.route :as route]
        [delivery-robot.graph :as g]))

(defn flat
  [s]
  (mapcat
    #(if (every? coll? %)
       (flat %)
       (list %)) s))

(defn get-routes
  ([graph a b] (get-routes graph (g/get-node a graph) [a] (g/not-visited (get (g/get-node a graph) :neighbours) [a]) b))
  ([graph curr route next-moves dest]
   (if (= (get curr :name) dest)
    route
    (flat
      (map #(get-routes 
              graph
              (g/get-node (%1 :name) graph)
              (conj route (get %1 :name))
              (g/not-visited (get (g/get-node (% :name) graph) :neighbours) (conj route (get %1 :name)))
              dest) next-moves)))))

(defn get-best-route
  [graph a b]
  (first (sort-by count (get-routes graph a b))))

(get-routes (g/load-from-file "graph.txt") "o107" "mail")

