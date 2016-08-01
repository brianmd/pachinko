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

(defn process-nasa-page [m]
  (let [rows (html/select m [:table :tr])
        tz (t/time-zone-for-id (str "America/" "Denver"))
        ]
    (map (partial parse-row tz) (drop 1 rows))))

(defn find-next-sighting [m date]
  (let [d (c/to-long date)]
    (first (filter #(< d (c/to-long (first %))) (process-nasa-page m)))))





(def parsed (snarf-nasa-page "United States" "New Mexico" "Albuquerque"))
(find-next-sighting parsed (l/local-now))

