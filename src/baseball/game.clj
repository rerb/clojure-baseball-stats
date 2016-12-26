(ns baseball.game
  (:require [baseball.team :refer [team-for-id
                                   team-id-in-filename]])
  (:require [baseball.player :refer [player-for-id]])
  (:gen-class))

(defn- empty-lineup
  "Returns an empty lineup."
  ([dh?] (let [base-lineup {1 nil 2 nil 3 nil 4 nil 5
                            nil 6 nil 7 nil 8 nil 9 nil}]
           (if dh?
             (assoc base-lineup 10 nil)
             base-lineup)))
  ([] (empty-lineup nil)))

(defn new-game []
  "Returns a new game map."
  {:home-team nil
   :visiting-team nil
   :home-lineup (empty-lineup)
   :visiting-lineup (empty-lineup)
   :innings []})

(defn players [game all-players teams]
  "Returns the union of the home and visiting team rosters."
  (into (:roster (team-for-id teams (:hometeam game)))
        (:roster (team-for-id teams (:visteam game)))))

(defn pitching-lineup [game all-players teams]
  (let [last-pitch (last (:pitches game))
        batter-id (:batter-id last-pitch)
        players-in-game (players game all-players teams)
        batter (player-for-id batter-id players-in-game)]
    (if (= (:team-id batter) (:visteam game))
      (:home-lineup game)
      (:visiting-lineup game))))

(defn player-at-position [position lineup]
  "Returns the player playing position `postion` in lineup `lineup`."
  (if (empty? lineup)
    nil
    (let [slot (second (first lineup))
          this-position (:position slot)]
      (if (= this-position position)
        (:player slot)
        (player-at-position position (rest lineup))))))

(defn pitcher [game]
  (let [last-pitch (last (:pitches game))
        pitching-lineup (pitching-lineup game)]
    (player-at-position "P" pitching-lineup)))

(defn batter [game]
  'splat)

(defn pitch [pitcher batter code]
  "Returns a pitch for code `code` thrown by `pitcher` to `batter`."
  (let [this-pitch {:pitcher pitcher :batter batter}]
    (cond (= code \C) (assoc this-pitch :pitch "strike" :kind "called")
          (= code \S) (assoc this-pitch :pitch "strike" :kind "swinging")
          (= code \F) (assoc this-pitch :pitch "foul")
          (= code \B) (assoc this-pitch :pitch "ball"))))

(defn plays-for-pitch-codes [pitches pitcher batter]
  "Returns plays for each pitch in `pitches`."
  (map (partial pitch pitcher batter) pitches))
