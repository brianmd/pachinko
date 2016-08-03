(ns pachinko.spacestation
  (:require [clj-http.client :as client]
            [net.cgrand.enlive-html :as html]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-time.coerce :as c]
            [clj-time.local :as l]
            [clojure.string :as str]))

(defn html->map [s] (html/html-snippet s))

(def nasa-date-formatter (f/formatter "YYYY EEE MMM d, h:m a"))
(defn nasa-date [s tz]
  (t/from-time-zone (f/parse nasa-date-formatter (str "2016 " s)) tz))

(defn determine-timezone [country state city]
  ;; TODO: generalize to get correct timezone (defaults to MST for now)
  (let [tz-loc "America/Denver"]
    (t/time-zone-for-id tz-loc)))

(defn- snarf-nasa-page [country state city]
  (let [country (str/join "_" (str/split country #" "))
        state (str/join "_" (str/split state #" "))
        city (str/join "_" (str/split city #" "))
        url (str "https://spotthestation.nasa.gov/sightings/view.cfm?country=" country "&region=" state "&city=" city "#.V56l0GQrJNQ")
        response (client/get url)]
    (if (= (:status response) 200)
      (html->map (:body response)))))

(defn- extract-nasa-row-maps [m]
  (html/select m [:table :tr]))

(defn parse-row [tz row]
  (let [info (html/select row [:td :> html/text-node])]
    (conj (take 4 (drop 1 info)) (nasa-date (first info) tz))))

(defn process-nasa-page [tz m]
  (let [rows (html/select m [:table :tr])]
    (map (partial parse-row tz) (drop 1 rows))))

;; (defn find-next-sighting [m date]
;;   (let [d (c/to-long date)]
;;     (first (filter #(< d (c/to-long (first %))) (process-nasa-page m)))))

(defn- date-format [tz format date]
  (f/unparse (f/with-zone (f/formatter format) tz)
             date))




(re-find #"\d+" "< 1 min")




(defn- date-as-str [country state city date]
  (let [tz (t/time-zone-for-id "America/Denver")
        date (l/to-local-date-time date)
        dow (date-format tz "EEEE" date)
        hour (date-format tz "K" date)
        minute (date-format tz "m" date)
        am-or-pm (date-format tz "a" date)
        ]
    (str "The next sighting in " city " " state " is " dow " at " hour " " minute " " am-or-pm ".")))

;; (l/to-local-date-time (first x))
;; (date-as-str "a" "b" "c" (first x))
;; ("The next sighting in Albuquerque New Mexico is Tuesday at 4 28 AM" #<DateTime 2016-08-08T22:28:00.000-06:00> "< 1 min" "13°" "10° above  N" "13° above  N")

(defn find-next-sighting [country state city]
  (let [tz (determine-timezone country state city)
        page (snarf-nasa-page country state city)
        sightings (process-nasa-page tz page)
        date (t/to-time-zone (t/now) tz)
        now-date (c/to-long date)
        now (c/to-long now-date)
        next-sighting (first (filter #(< now (c/to-long (first %))) sightings))
        ]
    (conj next-sighting (date-as-str country state city (first next-sighting)))
    ))

;; (def parsed (snarf-nasa-page "United States" "New Mexico" "Albuquerque"))
;; (find-next-sighting "United States" "New Mexico" "Albuquerque")
;; (def x (find-next-sighting "United States" "New Mexico" "Albuquerque"))
;; (def y (find-next-sighting "United States" "New Mexico" "Albuquerque"))

;; "The next sighting in Albuquerque New Mexico is Monday at3:22 PM"

;; (#<DateTime 2016-08-08T22:28:00.000-06:00> "< 1 min" "13°" "10° above  N" "13° above  N")

;; (f/unparse (f/formatter "d") (first x))
;; (f/unparse (f/with-zone (f/formatter "d")
;;              (t/time-zone-for-id "America/Denver"))
;;            (first x))
