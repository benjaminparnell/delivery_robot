(ns delivery-robot.robot
  (:use [delivery-robot.graph :as g]
        [delivery-robot.util :as util]
        [io.aviso.ansi]))

(defn no-graph
  [robot]
  (dissoc robot :graph))

(defn spawn
  "spawn sets up the robot hash-map with the required state to get started"
  [graph]
  (hash-map :position "office", :packages '(), :destinations [], :graph graph))

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

(defn destinations
  "destinations returns the current destinations of the robot.
  These are rooms with packages in, not packages."
  [robot]
  (get robot :destinations))

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
  (assoc robot :destinations (into (destinations robot) packs)))

(defn prn-state
  [robot]
  (println (bold-white "Robot State:") (bold-red (no-graph robot))))

(defn room-has-packages
  "Checks if a room has packages"
  [room]
  (seq (room :packages)))

(defn scan
  "Scan a graph for packages and return a list of where they are
  (Not where they are going to)"
  [graph]
  (distinct
    (reduce (fn [packages node]
              (let [has-packages (room-has-packages node)]
                (if has-packages
                  (into packages [(node :name)])
                  packages))) [] graph)))

(defn pick-up-packages
  [robot room]
  (assoc robot
         :packages (into (robot :packages) (room :packages))
         :destinations (vec (distinct (into (room :packages) (destinations robot))))))

(defn clear-room
  [robot room]
  (assoc
    robot
    :graph
    (map
      (fn [node]
        (if (= (node :name) (room :name))
          (assoc node :packages [])
          node))
      (robot :graph))))

(defn calculate-route
  [robot]
  (let [perms (g/perms (robot :position) (destinations robot))
        route (g/get-best-perm perms (robot :graph))]
    route))

(defn recalculate
  [route calculate robot room]
  (if calculate
    (rest (calculate-route (clear-room (pick-up-packages robot room) room)))
    (rest route)))

(defn pick-up-from-office
  [robot]
  (let [office (g/get-node "office" (robot :graph))]
    (clear-room
      (assoc robot
         :packages (into (robot :packages) (office :packages))
         :destinations (vec (distinct (into (office :packages) (destinations robot))))) office)))

(defn clean-destinations
  [robot]
  (if (util/in? (destinations robot) (robot :position))
    (assoc robot :destinations (vec (remove #(= (robot :position) %) (destinations robot))))
    robot))

(defn go
  [robot]
  (let [perms (g/perms (robot :position) (destinations robot))
        route (g/get-best-perm perms (robot :graph))]
    (println (bold-cyan (format "Start at %s" (first (rest route)))))
    (loop [route (rest route)
           room (g/get-node (first (rest route)) (robot :graph))
           room-name (room :name)
           robot robot]
      (println (bold-cyan "Move to") (bold-green room-name))
      (prn-state (move robot room-name))
      (if (room-has-packages room)
        (println (bold-yellow (format "Picking up packages %s from %s" (room :packages) room-name))))
      (if (has-package-for robot room-name)
        (println (bold-yellow (format "Dropping package in %s" room-name))))
      (if (and (= 1 (count (rest route))) (not (room-has-packages room)))
        (do
          (println (bold-green "Done."))
          (move (drop-packages robot (room :name)) room-name))
        (let [robot (clean-destinations (move (drop-packages robot room-name) room-name))
              route (recalculate route (room-has-packages room) robot room)
              next-room (g/get-node (nth route 1) (robot :graph))]
          (if (room-has-packages room)
            (recur
              route ; The route has changed, so calculate a new one.
              next-room
              (next-room :name)
              (clear-room (pick-up-packages robot room) room))
            (recur route next-room (next-room :name) robot)))))))

