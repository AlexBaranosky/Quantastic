(ns quant.test.core
  (:use [quant.core :only (ric-from-params extract-via-scrape month-str->num third fourth fifth lowercase-keyword)]
        [apricot-soup :only ($ s-expressions)]
        [midje.sweet])
  (:import [org.joda.time DateMidnight]
           [org.w3c.tidy Tidy]
           [java.io StringWriter StringReader]))

(fact "can generate lower-case keywords from a string"
  (lowercase-keyword "BobCratchet") => :bobcratchet
  (lowercase-keyword "Bob Cratchet") => :bob-cratchet)

(tabular
  (fact "extra english ways of getting nth element of a seq"
    (?nth [1 2 3 4 5 6]) => ?element)

  ?nth   ?element
  third  3
  fourth 4
  fifth  5)

(fact "pulls RIC out of link params"
  (ric-from-params "?symbol=GOOG.O&name=ARORA+NIKESH") => "GOOG.O")

(def sample-page (slurp "./test/quant/test/sample-page.html"))

(fact "pulls list of transaction info from Reuters Insider Trading html page"
  (take 2 (extract-via-scrape sample-page)) => [ { :ric "GOOG.O"
                                                   :direction :buy
                                                   :dollar-amount 0.0
                                                   :shares 934
                                                   :transaction-date (DateMidnight. 2011 10 14) }
                                                 { :ric "GOOG.O"
                                                   :direction :sell
                                                   :dollar-amount 598.75
                                                   :shares 2266
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