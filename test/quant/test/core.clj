(ns quant.test.core
  (:use [quant.core]
        [apricot-soup]
        [midje.sweet])
  (:import [org.joda.time DateMidnight]
           [org.w3c.tidy Tidy]
           [java.io StringWriter StringReader]))

(defn- pretty-printing-jtidy []
  (doto (Tidy.)
    (.setSmartIndent true)
    (.setTrimEmptyElements true)
    (.setShowWarnings false)
    (.setXmlOut true)
    (.setQuiet true)))

(defn illformed-html->xhtml [illformed-html]
  (let [writer (StringWriter.)]
    (.parse (pretty-printing-jtidy) (StringReader. illformed-html) writer)
    (str writer)))

(fact "asdf"
  (illformed-html->xhtml "<html>\n  <head>\n    <meta name=\"generator\"\n    content=\"HTML Tidy for Java (vers. 2009-12-01), see jtidy.sourceforge.net\" />\n    <title></title>\n  </head>\n  <body>\n    <img src=\"\" />\n  </body>\n</html>\n"))

(defn ric-from-params [param-string]
  (second (re-matches #"^.*\?symbol=([^&]+).*$" param-string)))

(fact "pulls RIC out of link params"
  (ric-from-params "?symbol=GOOG.O&name=ARORA+NIKESH") => "GOOG.O")

(defn third [coll]
  (nth coll 2))

(defn extract-via-scrape
  "Extracts trade-idea data from an html source"
  [html]
  (let [trs (map third ($ (illformed-html->xhtml html) "tr" tr (s-expressions)))
        tr->trade-idea (fn [[td1 td2 _td3_ td4 _td5_ td6]]
                          { :ric (ric-from-params (-> td2 third first third first second :href))
                            :direction (-> td4 third)
                            :amount (-> td6 third)
                            :transaction-date (-> td1 third) } )]
    (map tr->trade-idea trs)))

(def sample-page (slurp "./test/quant/test/sample-page.html"))

(def sample-trs
  "<table>
    <tr class=\"dataSmall stripe\">
      <td>14 Oct 2011</td>
      <td>
        <h2 class=\"officers\">
          <a href=\"?symbol=GOOG.O&name=ARORA+NIKESH\">ARORA NIKESH</a>
        </h2>
      </td>
      <td>Officer</td>
      <td> Buy</td>
      <td>
        <a href=\"insiderTradingDetails?symbol=GOOG.O&accession=111146730&trade_no=4\">934</a>
      </td>
      <td>$0.00</td>
    </tr>
    <tr class=\"dataSmall \">
      <td>13 Oct 2011</td>
      <td><h2 class=\"officers\"><a href=\"?symbol=GOOG.O&name=ARORA+NIKESH\">SECOND OFFICER</a></h2></td>
      <td>Officer</td>
      <td> Sell</td>
      <td><a href=\"insiderTradingDetails?symbol=GOOG.O&accession=111146730&trade_no=1\">2,266</a></td>
      <td>$598.75</td>
    </tr>
	<table>")

(fact "pulls list of transaction info from html string"
  (extract-via-scrape sample-trs) => [ { :ric "GOOG.O"
                                         :direction "Buy"
                                         :amount "$0.00"
                                         :transaction-date "14 Oct 2011" }
                                       { :ric "GOOG.O"
                                         :direction "Sell"
                                         :amount "$598.75"
                                         :transaction-date "13 Oct 2011" } ])

;;:transaction-date (DateMidnight. 2011 10 14) } ])

;(fact "pulls list of transaction info from html string"
;  (take 2 (extract-via-scrape sample-page)) => [ { :ric "GOOG.O"
;                                                  :direction "Buy"
;                                                  :amount "$0.00"
;                                                  :transaction-date "14 Oct 2011" }
;                                                { :ric "GOOG.O"
;                                                  :direction "Sell"
;                                                  :amount "$598.75"
;                                                  :transaction-date "13 Oct 2011" } ])
