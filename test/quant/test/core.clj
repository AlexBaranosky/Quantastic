(ns quant.test.core
  (:use [quant.core :only (ric-from-params extract-via-scrape month-str->num)]
        [apricot-soup :only ($ s-expressions)]
        [midje.sweet])
  (:import [org.joda.time DateMidnight]
           [org.w3c.tidy Tidy]
           [java.io StringWriter StringReader]))

(def sample-page (slurp "./test/quant/test/sample-page.html"))

(fact "pulls RIC out of link params"
  (ric-from-params "?symbol=GOOG.O&name=ARORA+NIKESH") => "GOOG.O")

(fact "pulls list of transaction info from html string 2"
  (take 2 (extract-via-scrape sample-page)) => [ { :ric "GOOG.O"
                                                  :direction "Buy"
                                                  :amount "$0.00"
                                                  :transaction-date (DateMidnight. 2011 10 14) }
                                                { :ric "GOOG.O"
                                                  :direction "Sell"
                                                  :amount "$598.75"
                                                  :transaction-date (DateMidnight. 2011 10 13) } ])

(tabular
  (fact "string month represenations map to 1-based month numbers"
    (month-str->num ?month-str) => ?month-num)

  ?month-str ?month-num
  "Jan"      1
  "Feb"      2
  "Mar"      3
  "Apr"      4
  "May"      5
  "Jun"      6
  "Jul"      7
  "Aug"      8
  "Sep"      9
  "Oct"      10
  "Nov"      11
  "Dec"      12)