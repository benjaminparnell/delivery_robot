(ns delivery-robot.core
  (:use [delivery-robot.robot :as robot]
        [delivery-robot.graph-loader :as gl]
        [io.aviso.ansi]))

; Load graphs into variables for ease of use
(def assignment-graph (gl/load-from-file "graph.txt"))
(def test-graph (gl/load-from-file "test-graph.txt"))

; Move a robot
(def kevin (robot/spawn assignment-graph))

(def assignment-scenarios [{:name "Scenario 1" :packages [{:from "office" :to "r131"}]}
                           {:name "Scenario 2" :packages [{:from "office" :to "r119"}]}
                           {:name "Scenario 3" :packages [{:from "r131" :to "r115"}]}
                           {:name "Scenario 4" :packages [{:from "r131" :to "r129"}]}
                           {:name "Scenario 5" :packages [{:from "r131" :to "office"}]}
                           {:name "Scenario 6" :packages [{:from "office" :to "r131"} {:from "office" :to "r111"}]}
                           {:name "Scenario 7" :packages [{:from "office" :to "r131"} {:from "office" :to "r111"} {:from "r121" :to "office"}]}
                           ])

(defn place-scenario-packages
  "Place packages from a scenario in a graph before its run"
  [packages graph]
  (map (fn [room]
    (let [from-packages (filter #(= (% :from) (room :name)) packages)]
     (if (empty? from-packages)
       room
       (update-in room [:packages] into (map :to from-packages))))) graph))

(defn run-scenarios
  "Run scenarios and print output to the screen"
  [scenarios]
  (doseq [scenario scenarios]
    (let [graph (place-scenario-packages (scenario :packages) assignment-graph)
          rbt (pick-up-from-office (assoc kevin :graph graph))]
      (println (red-bg (format "\n--- START %s ---\n" (scenario :name))))
      (robot/go (robot/schedule (robot/scan (rbt :graph)) rbt))
      (println (red-bg (format "\n--- END %s ---\n" (scenario :name)))))))

(defn -main
  [& args]
  (run-scenarios assignment-scenarios))
