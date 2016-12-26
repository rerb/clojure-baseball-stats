(ns baseball.loader
  (:require [baseball.game :refer [new-game
                                   player-at-position
                                   plays-for-pitch-codes]])
  (:require [baseball.player :refer [player-for-id]])
  (:require [baseball.util :refer [position-number-to-abbreviation]])
  (:gen-class))

(defn record-type [record]
  (first (clojure.string/split record #",")))

(defn split-record [record]
  (clojure.string/split record #","))

(defn is-visitor? [i]
  (= i 0))

(defn handle-id-record [& _]
  "Returns a new game.
Ignores its argument, since it doesn't need it, but accepts it since
all the other handle-*-record fns accept at least the record as an
arg."
  (new-game))

(defn handle-version-record [record game]
  "Returns a game map updated with version number."
  (let [version (last (clojure.string/split record #","))]
    (assoc game :version version)))

(defn handle-info-record [record game teams players]
  "Returns a game map updated with the info . . . info."
  (let [record-data (rest (clojure.string/split record #","))
        key (first record-data)
        value (last record-data)]
    (assoc game (keyword key) value)))

(defn handle-start-record [record game players]
  "Returns a game with a lineup updated with the start record data."
  ;; There are 18 (for the NL or pre-DH AL) or 20 (for the
  ;; AL with the DH) start records, which identify the
  ;; starting lineups for the game.  Each start record has
  ;; five fields.
  ;;
  ;; 1. The first field is the Retrosheet ID code,
  ;;    which is unique for each player.  This 8 digit code is
  ;;    constructed from the first four letters of the player's
  ;;    last name, the first initial of his common name, and a
  ;;    three digit number.
  ;;
  ;; 2. The second field is the player's name.
  ;;
  ;; 3. The next field is either 0 (for visiting team), or 1
  ;;    (for home team).
  ;;
  ;; 4. The next field is the position in the batting order.
  ;;
  ;; 5. The last field is the starting fielding position.  The
  ;;    numbers are the standard notation, with designated
  ;;    hitters being identified as position 10.
  (let [[_ player-id _ visiting-or-home batting pos-num] (split-record record)
        lineup-key (if (is-visitor? visiting-or-home)
                     :visiting-lineup
                     :home-lineup)
        lineup (lineup-key game)
        position (position-number-to-abbreviation pos-num)]
    (assoc game lineup-key
           (assoc lineup
             (Integer/parseInt batting)
             {:player (player-for-id player-id players)
              :position position}))))

(defn handle-play-record [record games teams players]
  "Returns games reflecting the play in `record`."
  ;; The play records contain the events of the game.  Each
  ;; play record has 7 fields.
  ;; 1. The first field is the inning.
  ;; 2. The second field is either 0 (for visiting team) or 1
  ;;    (for home team).
  ;; 3. The third field is the Retrosheet ID code.
  ;; 4. The fourth field is the count on the batter when this
  ;;    particular event (play) occurred.  Most Retrosheet
  ;;    games do not have this information, and in such cases,
  ;;    "??" appears in this field.
  ;; 5. The fifth field is of variable length and contains all
  ;;    pitches to this batter in this plate appearance.  The
  ;;    standard pitches are:  C for called strike, S for
  ;;    swinging strike, B for ball, F for foul ball.  In
  ;;    addition, pickoff throws are indicated by the number of
  ;;    the base the throw went to.  For example, "1" means the
  ;;    pitcher made a throw to first, "2" a throw to second,
  ;;    etc.  If the base number is preceded by a "+" sign, the
  ;;    pickoff throw was made by the catcher. Some of the less
  ;;    common pitch codes are L:foul bunt, M:missed bunt,
  ;;    Q:swinging strike on a pitchout, R:foul ball on a pitchout,
  ;;    I:intentional ball, P:pitchout, H:hit by pitch, K:strike of
  ;;    unknown type, U:unkown or missing pitch.  Most Retrosheet
  ;;    games do not have pitch data and consequnetly this field
  ;;    is blank for such games.
  ;;
  ;;    There is occasionally more than one event for each
  ;;    plate appearance, such as stolen bases, wild pitches,
  ;;    and balks in which the same batter remains at the
  ;;    plate.  On these occasions the pitch sequence is
  ;;    interrupted by a period, and there is another play
  ;;    record for the resumption of the batter's plate
  ;;    appearance.
  ;; 6. The sixth field describes the play which occurred.
  ;;    This field is variable in length and has three main
  ;;    portions which follow the Retrosheet scoring
  ;;    system.  The scoring procedure description also
  ;;    contains a diagram that explains clearly how each area
  ;;    of the playing field is designated.  (The diagram is
  ;;    available under Docs, but most Retrosheet game
  ;;    accounts do not contain detailed location codes.)
  ;;      a. The first portion is a description of the basic
  ;;         play, following standard baseball scoring
  ;;         notation.  For example, a fly ball to center field
  ;;         is "8", a ground ball to second is "43", etc.
  ;;         Base hits are abbreviated with a letter (S for
  ;;         singles, D for doubles, T for triples, H for home
  ;;         runs) and (usually) a number identifying the
  ;;         fielder who played the ball.  Therefore "S7" is a
  ;;         single fielded by the left fielder.
  ;;      b. The second portion is a modifier of the first part
  ;;         and is separated from it with a forward slash,
  ;;         "/".  In fact, there may be more than one second
  ;;         portion.  Typical examples are hit locations.  For
  ;;         example, "D8/78" indicates a double fielded by the
  ;;         center fielder on a ball hit to left center.
  ;;         Other possible second portion modifiers are "SH"
  ;;         for sacrifice hits, GDP for grounding into double
  ;;         plays, etc.
  ;;      c. The third portion describes the advancement of any
  ;;         runners, separated from the earlier parts by a
  ;;         period.  For example, "S9/L9S.2-H;1-3"  should be
  ;;         read as: single fielded by the right fielder, line
  ;;         drive  to short right field.  The runner on 2nd
  ;;         scored (advanced to home), and the runner on first
  ;;         advanced to third.  Note that any advances after
  ;;         the first are separated by semicolons.
  (let [[_ inning _ player-id count pitch-codes play] (split-record record)
        game (peek games)
        batter (player-for-id player-id players)
        pitching-lineup (if (= (:team batter) (:visteam game))
                          (:home-lineup game)
                          (:visiting-lineup game))
        pitcher (player-at-position "P" pitching-lineup)
        old-plays (:plays game)
        new-plays (plays-for-pitch-codes pitch-codes pitcher batter)]
    (conj (rest games)
          (assoc game :plays (into old-plays new-plays)))))

(defn handle-com-record [record game teams players] :com)
(defn handle-sub-record [record game teams players] :sub)
(defn handle-data-record [record game teams players] :data)

(defn handle-events
  ([events teams players]
     (handle-events events [] teams players))
  ([events games teams players]
     (let [record (first events)
           record-type (record-type record)
           game (peek games)]

       (cond (= record-type "id")
             (handle-events (rest events)
                          (conj games (handle-id-record))
                          teams
                          players)

             (= record-type "version")
             (handle-events (rest events)
                          (conj (pop games)
                                (handle-version-record record game))
                          teams
                          players)

             (= record-type "info")
             (handle-events (rest events)
                          (conj (pop games)
                                (handle-info-record record game teams players))
                          teams
                          players)

             (= record-type "start")
             (handle-events (rest events)
                          (conj (pop games)
                                (handle-start-record record game players))
                          teams
                          players)

             (= record-type "play") games))))

(defn load-events [filename]
  (map clojure.string/trim
       (clojure.string/split (slurp filename)
                             #"\n")))
