(ns baseball.team
  (:require [baseball.player :refer [player]])
  (:require [baseball.util :refer [indexed-files]])
  (:use org.satta.glob)
  (:gen-class))

(defn team [record]
  "Returns a team map bursted out of `record`."
  (let [[id league city name] (clojure.string/split record #",")]
    {:id id :league league :city city :name name}))

(defn- teams-for-lines [lines]
  "Returns a list of teams created from `lines`."
  (if (empty? lines)
    []
    (let [this-team (team (clojure.string/trim (first lines)))
          this-team-id (:id this-team)]
      (conj (teams-for-lines (rest lines))
            this-team))))

(defn load-teams [filename]
  "Returns a list of teams loaded from file `filename`."
  (let [file-contents (slurp filename)
        lines (clojure.string/split file-contents #"\n")]
    (teams-for-lines lines)))

(defn teams-indexed-by-id [teams]
  "Returns a map of `teams` indexed by id."
  (if (empty? teams)
    {}
    (let [this-team (first teams)]
      (conj (hash-map (:id this-team) this-team)
            (teams-indexed-by-id (rest teams))))))

(defn team-for-id [teams id]
  "Returns the team with id `id`."
  (get (teams-indexed-by-id teams) id))

(defn team-id-in-filename [file]
  "Returns the team ID contained in `filename`."
  (let [filename (.getName file)]
    (subs filename 0 3)))

(defn team-for-roster-file [teams file]
  "Returns the team represented in `file`."
  (let [team-id (team-id-in-filename file)]
    (team-for-id teams team-id)))

(defn roster-file-for-team
  "Returns the file in roster-files that's for `team`."
  [roster-files team]
  (let [file-map (indexed-files roster-files)
        file-name (first (filter #(= (subs %1 0 3) (:id team))
                                 (keys file-map)))]
    (get file-map file-name)))

(defn- roster-from-records [records]
  "Returns a vector of players created from `records`."
  (if (empty? records)
    []
    (conj (roster-from-records (rest records))
          (player (clojure.string/trim (first records))))))

(defn load-roster-file [file]
  "Returns a roster of players loaded from file `file-name`."
  (roster-from-records (clojure.string/split (slurp file)
                                             #"\n")))

(defn load-team-roster [team roster-file]
  "Returns a team with a roster loaded from roster-file."
  (assoc team :roster
         (load-roster-file roster-file)))

(defn load-teams-rosters [teams roster-files]  ;; TODO - rename
  "Load rosters for each team in `teams`."
  (if (empty? teams)
    []
    (let [team (first teams)
          roster-file (roster-file-for-team roster-files team)]
      (conj (load-teams-rosters (rest teams) roster-files)
            (load-team-roster team roster-file)))))
