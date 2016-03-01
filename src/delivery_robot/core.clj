(ns delivery-robot.core
  (:use [delivery-robot.robot :as robot]
        [delivery-robot.graph :as g]))

; Load graphs into variables for ease of use
(def assignment-graph (g/load-from-file "graph.txt"))
(def test-graph (g/load-from-file "test-graph.txt"))

; Move a robot
(def kevin (robot/spawn assignment-graph))

(let [kevin (robot/schedule ["d1" "r111" "storage"] kevin)]
  (robot/no-graph (robot/go kevin)))
