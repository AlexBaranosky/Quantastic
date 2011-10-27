(ns quant.core
  (:use [apricot-soup :only ($ s-expressions)]))

(defn- third [coll]
  (nth coll 2))

(defn ric-from-params [param-string]
  (second (re-matches #"^.*\?symbol=([^&]+).*$" param-string)))

(defn extract-via-scrape
  "Extracts trade-idea data from an html source"
  [html]
  (let [trs (map third ($ html "tr[class*=dataSmall]" (s-expressions)))
        tr->trade-idea (fn [[td1 td2 _ td4 _ td6]]
                          {
                            :ric (ric-from-params (-> td2 third first third first second :href))
                            :direction (third td4)
                            :amount (third td6)
                            :transaction-date (third td1)
                          } )]
    (map tr->trade-idea trs)))