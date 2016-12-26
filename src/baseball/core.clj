(ns baseball.core
  ;; (:require [datomic.api :as d :refer [db q]])
  ;; (:require [baseball.team :refer [load-team-roster
  ;;                                  load-teams
  ;;                                  roster-file-for-team]])
  ;;                                  team-for-id
  ;;                                  team-id-in-filename]])
  ;; (:require [baseball.player :refer [player-for-id]])
  ;; (:require [baseball.game :refer [batter
  ;;                                  new-game
  ;;                                  pitcher
  ;;                                  pitching-lineup
  ;;                                  player-at-position
  ;;                                  team-for-roster-file]])
  ;; (:require [baseball.util :refer [indexed-files]])
  ;; (:use org.satta.glob)
  (:gen-class))


;; (defn -main
;;   "I don't do a whole lot ... yet."
;;   [& args]
;;   (use 'korma.db)
;;   (println "Hello, World!"))

;;
;; testing
;;

;; (defn load-test-games []
;;   (let [teams (load-teams "/Users/rerb/Downloads/2013eve/TEAM2013")
;;         pirates (load-roster "/Users/rerb/Downloads/2013eve/PIT2013.ROS"
;;                              teams)
;;         cubs (load-roster "/Users/rerb/Downloads/2013eve/CHN2013.ROS"
;;                           teams)
;;         players (into pirates cubs)
;;         events (load-events "/Users/rerb/Downloads/2013eve/2013PIT.EVN")]
;;     (handle-events events teams players)))

;;;;;;;;;;;;;;;;;;;;;;;;;

;; (def data-directory "/Users/rerb/Downloads/2013eve/")

;; (def rosterless-teams (load-teams (str data-directory "TEAM2013")))

;; (def roster-files (glob (str data-directory "*.ROS")))

;; (def teams (load-teams-rosters rosterless-teams rosterfiles))
