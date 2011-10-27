(ns quant.test.core
  (:use [quant.core :only (ric-from-params extract-via-scrape)]
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
                                                  :transaction-date "14 Oct 2011" }
                                                { :ric "GOOG.O"
                                                  :direction "Sell"
                                                  :amount "$598.75"
                                                  :transaction-date "13 Oct 2011" } ])

;; TODO:
;;:transaction-date (DateMidnight. 2011 10 14) } ])
