(ns portfolio.markit
  (:require [clj-http.client :as http-client]
            [clojure.data.json :as json]))

(def api-url "http://dev.markitondemand.com/Api/v2/Quote/json")

(defn fetch-quote [symbol]
  (http-client/get api-url {:query-params {:symbol symbol}}))

(defn last-price [symbol]
  (-> (:body (fetch-quote symbol))
      (json/read-str)
      (get "LastPrice")))

(defn price-map [symbols]
  (into {} (pmap (juxt identity last-price) symbols)))
