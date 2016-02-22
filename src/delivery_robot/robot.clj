(ns delivery-robot.robot)

(defn spawn
  "spawn sets up the robot hash-map with the required state to get started"
  []
  (hash-map :position "", :packages '()))

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
