(ns quant.core
  (:use [apricot-soup :only ($ s-expressions)]
        [clojure.contrib.string :only (replace-str)])
  (:import [org.joda.time DateMidnight]))

(def third (comp first rest rest))
(def fourth (comp first rest rest rest))
(def fifth (comp first rest rest rest rest))

(def month-str->num
  {"Jan" 1 "Feb" 2 "Mar" 3 "Apr" 4 "May" 5 "Jun" 6 "Jul" 7 "Aug" 8 "Sep" 9 "Oct" 10 "Nov" 11 "Dec" 12})

(defn- date-from [s]
  (let [[_ day-of-month month-str year] (re-matches #"^\s*(\d\d)\s+(\w+)\s+(\d\d\d\d)\s*$" s)]
    (DateMidnight. (Integer/parseInt year) (month-str->num month-str) (Integer/parseInt day-of-month))))

(defn ric-from-params [param-string]
  (second (re-matches #"^.*\?symbol=([^&]+).*$" param-string)))

(defn lowercase-keyword [s]
  (keyword (.toLowerCase (replace-str " " "-" s))))

(defn extract-via-scrape
  "Extracts trade-idea data from an html source"
  [html]
  (let [trs (map third ($ html "tr[class*=dataSmall]" (s-expressions)))
        tr->trade-idea (fn [[td1 td2 _ td4 _ td6]]
                          {
                            :ric (ric-from-params (-> td2 third first third first second :href))
                            :direction (lowercase-keyword (third td4))
                            :dollar-amount (Double/parseDouble (.substring (third td6) 1))
                            :transaction-date (date-from (third td1))
                          } )]
    (map tr->trade-idea trs)))