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

(defn find-next-sighting [country state city]
  (let [tz (determine-timezone country state city)
        page (snarf-nasa-page country state city)
        sightings (process-nasa-page tz page)
        date (t/to-time-zone (t/now) tz)
        now-date (c/to-long date)
        now (c/to-long now-date)]
    (first (filter #(< now (c/to-long (first %))) sightings))
    ))

;; (def parsed (snarf-nasa-page "United States" "New Mexico" "Albuquerque"))
;; (find-next-sighting "United States" "New Mexico" "Albuquerque")

