(ns delivery-robot.route)

(defn add
  [node route]
  (into [] (flatten
    (vector
      (+ (first route) (node :cost))
      (conj (into [] (rest route)) (node :name))))))

