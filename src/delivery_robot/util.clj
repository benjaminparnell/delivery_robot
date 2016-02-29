(ns delivery-robot.util)

(defn flat
  [s]
  (mapcat
    #(if (every? coll? %)
       (flat %)
       (list %)) s))

(defn in?
  "true if coll contains elem"
  [coll elem]
  (some #(= elem %) coll))
