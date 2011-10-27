(ns quant.test.core
  (:use [quant.core]  
        [org.clojars.kyleburton.clj-xpath]
        [midje.sweet]))

(defn extract [html]

  ;; TODO : tidy up the html, or just use enlive instead yeah lets do that

  (let [trs ($x "//tr[contains(@class, 'dataSmall')]" html)
        doit (fn [td1 td2 td3 td4 td5 td6 & _ :as tr]
                      {:ric nil ;;(-> td5 )
                       :direction (first td4) 
                       :transaction-date nil })]
    (prn (map doit trs))))                   

(def sample-page (slurp "./test/quant/test/sample-page.html"))

(fact "pulls list of transaction info from html string"
  (extract sample-page) => [ { :ric "VOD.L"
                               :direction :buy
                               :transaction-date [12 31 2000] }])
