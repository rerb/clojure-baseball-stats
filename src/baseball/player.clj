(ns baseball.player
  (:gen-class))

(defn player [line]
  "Returns a player map bursted out of `line`."
  (let [[id lname fname bats throws team-id position]
        (clojure.string/split line #",")]
     {:id id :lname lname :fname fname :bats bats :throws throws
        :team-id team-id :position position}))

(defn player-for-id [id players]
  "Returns player in `players` with ID `id`."
  (first (filter #(= (:id %1) id ) players)))
