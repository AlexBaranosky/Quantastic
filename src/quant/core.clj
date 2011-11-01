(ns quant.core
  (:use [apricot-soup :only ($ s-expressions)]
        [clojure.contrib.string :only (replace-str)])
  (:import [org.joda.time DateMidnight]))

(def third (comp first rest rest))
(def fourth (comp first rest rest rest))
(def fifth (comp first rest rest rest rest))

(defn lowercase-keyword [s]
  (keyword (.toLowerCase (replace-str " " "-" s))))

(defn parse-int [s] (Integer/parseInt s))
(def remove-commas (partial replace-str "," ""))
(def remove-dollars (partial replace-str "$" ""))

(def month-str->num
  {"Jan" 1 "Feb" 2 "Mar" 3 "Apr" 4 "May" 5 "Jun" 6 "Jul" 7 "Aug" 8 "Sep" 9 "Oct" 10 "Nov" 11 "Dec" 12})

(defn- date-from [s]
  (let [[_ day-of-month month-str year] (re-matches #"^\s*(\d\d)\s+(\w+)\s+(\d\d\d\d)\s*$" s)]
    (DateMidnight. (parse-int year) (month-str->num month-str) (parse-int day-of-month))))

(defn ric-from-params [param-string]
  (second (re-matches #"^.*\?symbol=([^&]+).*$" param-string)))

(defn extract-via-scrape
  "Extracts trade-idea data from an html source"
  [html]
  (let [col->ric #(-> % third first third first second :href ric-from-params)
        col->shares #(-> % third first third remove-commas parse-int)
        tr->trade-idea (fn [[[_ _ date] ric-column _ [_ _ direction] shares-column [_ _ amount]]]
                          {
                            :ric (col->ric ric-column)
                            :direction (lowercase-keyword direction)
                            :dollar-amount (Double/parseDouble (remove-dollars amount))
                            :shares (shares-column->shares shares-column)
                            :transaction-date (date-from date)
                          } )
        trs (map third ($ html "tr[class*=dataSmall]" (s-expressions)))]
    (map tr->trade-idea trs)))