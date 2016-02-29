(ns delivery-robot.route)

(defn add
  [node route]
  (let [cost (+ (first route) (node :cost))]
    (into [] (concat [cost] (rest route)))))
