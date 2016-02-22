(ns delivery-robot.graph)

(defn create-node
  "create-node returns a hash map of a node name with no packages in it and
   no neighbours"
  [name]
  (hash-map :name name, :packages '(), :neighbours '()))

(defn add-neighbour
  "add-neighbour adds a neighbour to node with a cost of moving between
   node and neighbour of cost"
  [node neighbour cost]
  (update-in
    node
    [:neighbours]
    conj
    (hash-map :name neighbour, :cost cost)))

(defn get-node
  "get-node gets the node with name from graph"
  [name graph]
  (first
    (filter #(= (%1 :name) name) graph)))

