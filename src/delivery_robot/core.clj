(ns delivery-robot.core
  (:use [delivery-robot.robot :as robot]
        [delivery-robot.route :as route]
        [delivery-robot.graph :as g]))

; (g/get-routes (g/load-from-file "test-graph.txt") "a" "f")
; (g/get-best-route (g/load-from-file "test-graph.txt") "a" "f")

