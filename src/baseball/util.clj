(ns baseball.util
  (:gen-class))

(defn indexed-files [files]
  "Returns a map of `files` indexed by filename."
  (if (empty? files)
    {}
    (assoc (indexed-files (rest files))
      (.getName (first files))
      (first files))))

(defn position-number-to-abbreviation [number]
  ((keyword (str number)) {:1 "P" :2 "C" :3 "1B"
                           :4 "2B" :5 "3B" :6 "SS"
                           :7 "LF" :8 "CF" :9 "RF"
                           :10 "DH"}))
