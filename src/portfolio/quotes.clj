(ns portfolio.quotes
  (:require [clj-http.client :as http-client]
            [clojure.data.json :as json]
            [clojure.string :as string]))

(def api-url "http://query.yahooapis.com/v1/public/yql")

(defn api-call [params]
  (http-client/get api-url {:query-params params}))

(defn fetch-json [symbols]
  (let [symbol-list (string/join "," (map (partial format "\"%s\"") symbols))]
    (api-call {:q (format "select * from yahoo.finance.quotes where symbol in (%s)" symbol-list)
               :env "http://datatables.org/alltables.env"
               :format "json"})))

(defn fetch-quotes [symbols]
  (-> (:body (fetch-json symbols))
      (json/read-str :key-fn keyword)
      (get-in [:query :results :quote])))

(defn fetch-prices [symbols]
  (->> (fetch-quotes symbols)
       (map (juxt :Symbol #(Float/parseFloat (:Ask %))))
       (into {})))
