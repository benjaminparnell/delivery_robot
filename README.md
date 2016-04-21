# delivery_robot

# Installation

Assuming you have leiningen installed, you can pull the projects dependencies 
using the `lein deps` command.

# Running scenarios

To run the scenarios detailed in the assignment document, simply typing `lein
run` after pulling the dependencies will run them all.

The output will include the scenario being run, the moves the robot is
performing, details of the robot state, and messages when the robot has
dropped/picked up packages.

# Adding scenarios

Scenarios are ran via the core namespace. To add a scenario for the robot to run
(or to remove some that are already there) just add a new hash-map to the
assignment scenarios vector. See the existing scenarios for explanation on how
to define them.

## Code explanation

The robots state space is represented by a clojure hash-map. All functions
relating to the robot are contained in the robot namespace. The hash-map
contains the robots position, what packages it currently has to deliver, what
destinations it has (where packages need to be delivered to or picked up from),
and the graph the robot is moving around.

Lots of the code in the robot namespace is simply wrappers for calling get on
the robots state space. It also has a bunch of useful functions like
drop-packages, has-package-for and schedule.

The robot begins by scanning the graph for packages and picking up any packages
that are in the office (it always starts here). robot/schedule is then called in
order to add those packages/destinations to the correct places in the robots
state space. The robot still hasn't done any route calculation, but its ready to
go. To make the robot do all the route calculation and print out where its
going, robot/go is called, passing a robots state space.

robot/go is really where most of the main logic is done. This is where packages
are picked up and dropped off as the robot moves around the floor and where all
status messages regarding the robots position and current space are printed.
This is also where the route is recalculated if the robot has picked up a
package to ensure that the robot is always taking the most optimal route for the
payload that it currently has. The route is not recalculated every time, only
when something has changed.

All the code for planning the route around the graph and getting the most
optimal one is found in the graph namespace. The graph/get-routes method is used
to get the most optimal route between 2 points on the graph, using Dijkstra's
algorithm to generate all the possible routes for that. It returns a list of
lists, where each list is a number at the start (indicating the overall cost of
traversing the route), and each item after that is the actual room names in the
route.

The robot needs to be able to plan for multi point journeys and find the best
route for each journey. The graph namespace does this by getting all possible
permutations of a route, calculating the cost for all of them, and then sorting
those to get the best one.

An example of this might be a set of destinations like this:

:office -> :o101 -> :storage -> :d1 -> :office

The robot will always start and end at the office and can't finish anywhere
else, so those points never change. The graph namespace generates all
permutations of all other items using the clojure.math.combinatorics library,
gets the route between each point in each combination and works out the cost of
each route. The sorting mentioned above then takes place to get the cheapest one
in terms of cost.

The only other code to speak of is the graph_loader namespace. I wrote this to
load a graph in from a text file, written in a format I came up with. It follows
this schema:

<room> -> <neighbour>(optional cost), <neighbour>(optional cost)

It was a lot easier to write smaller graphs to test with and load then in in
this fashion. I have included a smaller graph like this in the text-graph.txt
file. The graph detailed in the assignment is written up in this format in the
graph.txt file, and the application loads the graph in in this way, so don't
modify it. There isn't really any validation on the schema, its just basic
parsing.

