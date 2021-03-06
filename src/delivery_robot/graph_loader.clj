(ns delivery-robot.graph-loader
  (:require [clojure.string :as str]))

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

