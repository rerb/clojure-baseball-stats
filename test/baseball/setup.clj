(ns baseball.test.setup
  (:require [baseball.team :refer [load-teams]])
  (:require [org.satta.glob :refer [glob]])
  (:gen-class))

(def data-directory "/Users/rerb/Downloads/2013eve/")
(def teams-file (str data-directory "TEAM2013"))
(def roster-files (glob (str data-directory "*.ROS")))
(def teams (load-teams teams-file))
