(ns delivery-robot.util)

(defn flat
  "Flattens a list of list of lists down to a list of lists."
  [s]
  (mapcat
    #(if (every? coll? %)
       (flat %)
       (list %)) s))

(defn in?
  "true if coll contains elem"
  [coll elem]
  (some #(= elem %) coll))
