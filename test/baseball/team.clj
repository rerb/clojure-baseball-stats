(ns baseball.test.team
  (:require [baseball.test.setup :as setup])
  (:require [baseball.team :refer :all])
  (:require [clojure.test :refer [deftest
                                  is
                                  run-tests]])
  (:require [org.satta.glob :refer [glob]])
  (:gen-class))

(deftest test-load-teams
  (is (> (count setup/teams))
      1))

(deftest test-team-for-id
  (is (= (:name (team-for-id setup/teams "PIT"))
         "Pirates")))

(deftest test-team-id-in-filename
  (is (= (team-id-in-filename (first setup/roster-files))
         "ANA")))

(deftest test-team-for-roster-file
  (is (= (team-for-roster-file setup/teams (first setup/roster-files))
         {:id "ANA", :league "A", :city "Anaheim", :name "Angels"})))

(deftest test-roster-file-for-team
  (let [pirates (team-for-id setup/teams "PIT")]
    (is (= (.getName (roster-file-for-team setup/roster-files pirates))
           (.getName (first (glob (str setup/data-directory "PIT*.ROS"))))))))

(deftest test-load-roster-file
  (is (> (count (load-roster-file (first setup/roster-files)))
         1)))

(deftest test-load-team-roster
  (is (< (count (:roster (load-team-roster (team-for-id setup/teams "PIT")
                                           (first setup/roster-files)))))
      1))

(deftest test-load-teams-rosters
  (let [teams (load-teams-rosters setup/teams setup/roster-files)]
    (is (= (count teams)
           30))
    (is (= (count (keys (first teams)))
           (count (keys (last teams)))))
    (is (> (count (:roster (first teams)))
           1))
    (is (> (count (:roster (last teams)))
           1))))

(run-tests)
