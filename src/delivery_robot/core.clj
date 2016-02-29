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
  ([graph a b] (get-routes graph (g/get-node a graph) [0 a] (g/not-visited (get (g/get-node a graph) :neighbours) [a]) b))
  ([graph curr route next-moves dest]
   (if (= (get curr :name) dest)
    route
    (flat
      (map #(get-routes 
              graph
              (g/get-node (%1 :name) graph)
              (route/add %1 route)
              (g/not-visited (get (g/get-node (% :name) graph) :neighbours) (route/add %1 route))
              dest) next-moves)))))

(defn get-best-route
  [graph a b]
  (first (sort-by first (get-routes graph a b))))

; (get-routes (g/load-from-file "test-graph.txt") "a" "f")
; (get-best-route (g/load-from-file "test-graph.txt") "a" "f")

