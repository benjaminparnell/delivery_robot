(ns delivery-robot.graph
  (:require [clojure.string :as str]
            [delivery-robot.util :refer :all]
            [delivery-robot.route :as route]))

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

(defn get-routes
  ([graph a b] (get-routes graph (get-node a graph) [0 a] (not-visited (get (get-node a graph) :neighbours) [0 a]) b))
  ([graph curr route next-moves dest]
   (if (= (get curr :name) dest)
    route
    (flat
      (pmap #(get-routes
              graph
              (get-node (get %1 :name) graph)
              (route/add %1 route)
              (not-visited (get (get-node (% :name) graph) :neighbours) (route/add %1 route))
              dest) next-moves)))))

(defn get-best-route
  ([routes] (first (sort-by first routes)))
  ([graph a b] (first (sort-by first (get-routes graph a b)))))

(defn parse-file
  "parse-file converts text into a data structure load-from-file can read"
  [file]
  (map #(zipmap [:name :neighbours] %1)
       (map #(map str/trim (str/split %1 #"->"))
            (str/split file #"\n"))))

(defn parse-neighbour-weight
  "parse-neighbour-weight checks for an optional weight in the neighbours name"
  [name]
  (let [match (re-seq #"\w+" name)]
    (if (= (count match) 1)
      (conj match 1)
      (conj match (Integer. (nth match 1))))))

(defn parse-neighbours
  "parse-neighbours turns parse-file neighbours into coll of neighbours"
  [node]
  (assoc
    node
    :neighbours
    (map 
      #(zipmap [:cost :name] (parse-neighbour-weight %1))
      (str/split (node :neighbours) #", "))))

(defn load-from-file
  "load-from-file loads a graph from a file"
  [path]
  (let [file (parse-file (slurp path))]
    (map #(assoc (parse-neighbours %1) :packages '()) file)))

; (get-node "ts" (load-from-file "graph.txt"))
