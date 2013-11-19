(ns portfolio.core
  (:require [portfolio.quotes :as quotes]))

(def allocation-file "src/portfolio/allocation.clj")

(def sum (partial reduce +))

(defn cash [n] (format "$%3.2f" n))

(defn read-allocation [] (:assets (read-string (slurp allocation-file))))

(defn find-prices [allocation]
  (quotes/fetch-prices (map :ticker allocation)))

(defn shares-for [dollars price percent]
  (/ (* percent dollars) price))

(defn distribute [dollars allocation]
  (let [prices (find-prices allocation)]
    (for [{symbol :ticker pct :percent} allocation]
      (let [price (get prices symbol)
            shares (int (shares-for dollars price (/ pct 100)))]
        {:symbol symbol
         :price price
         :shares shares
         :value (cash (* price shares))}))))

(defn report [dollars allocation]
  (let [distribution (distribute dollars allocation)
        total (sum (map #(* (:price %) (:shares %)) distribution))]
    {:assets distribution
     :value (cash total)
     :remainder (cash (- dollars total))}))
