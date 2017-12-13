(ns portfolio.quotes
  (:require [clj-http.client :as http-client]
            [clojure.data.json :as json]
            [clojure.string :as string]))

(def api-url "https://api.iextrading.com/1.0/stock/market/batch")

(defn api-call [params]
  (http-client/get api-url {:query-params params}))

(defn fetch-json [symbols]
  (let [symbol-list (string/join "," (map name symbols))]
    (api-call {:symbols symbol-list
               :types "quote"
               :last 1})))

(defn fetch-quotes [symbols]
  (-> (:body (fetch-json symbols))
      (json/read-str :key-fn keyword)))

(defn fetch-prices [symbols]
  (->> (fetch-quotes symbols)
       (map (fn [[k v]] [k (get-in v [:quote :latestPrice])]))
       (into {})))
