(ns delivery-robot.robot
  (:use [delivery-robot.route :as rt]
        [delivery-robot.util :as util]))

(defn no-graph
  [robot]
  (dissoc robot :graph))

(defn spawn
  "spawn sets up the robot hash-map with the required state to get started"
  [graph]
  (hash-map :position "office", :packages [], :graph graph))

(defn position
  "position returns the robots position string"
  [robot]
  (get robot :position))

(defn move
  "move returns a new robot that has been moved to pos"
  [robot pos]
  (assoc robot :position pos))

(defn packages
  "packages returns the packages a robot is carrying"
  [robot]
  (get robot :packages))

(defn add-package
  "add-package returns a new robot carrying the new package"
  [robot package]
  (assoc robot :packages (conj (packages robot) package)))

(defn has-package-for
  [robot room]
  (util/in? (packages robot) room))

(defn drop-packages
  [robot room]
  (assoc robot :packages (remove #(= room %) (packages robot))))

(defn schedule
  [packs robot]
  (assoc robot :packages (into (packages robot) packs)))

(defn go
  [robot]
  (let [perms (rt/perms (robot :position) (packages robot))
        route (rt/get-best-perm perms (robot :graph))]
    (prn "The best route costs " (first route))
    (prn "Start at " (first (rest route)))
    (loop [route (rest route)
           room (first (rest route))
           robot robot]
      (prn "Move to " room)
      (if (has-package-for robot room)
        (prn "Dropping package in " room))
      (if (= 1 (count (rest route)))
        (do
          (prn "Done.")
          (move (drop-packages robot room) room))
        (do 
          (recur (rest route) (nth (rest route) 1) (move (drop-packages robot room) room)))))))
