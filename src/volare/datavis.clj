(ns volare.datavis
  (:require [clojure.string :as str]
            [cheshire.core :as json]))

(defn display-flights
  [response-reader]
  (with-open [response-reader (:body :response)]
    (let [data (json/parse-stream response-reader true)])))
