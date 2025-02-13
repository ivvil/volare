(ns volare.gflights
  (:require [clj-http.client :as client]
            [clojure.string :as str]))

(def api-baseurl "https://serpapi.com/search")

(defn build-query
  [url params]
  (let [query-str (str/join "&" (map (fn [[k v]]
                                       (str (name k) "=" v))
                                     params))]
    (str url "?" query-str)))

(defn run-db-query
  [query]
  {:departure_id (:origin query)
   :arrival_id (:destiny query)
   :travel_class (:class query)
   :adults (:passenger_adults query)
   :children (:passenger_children query)
   :infants_in_seat (:passenger_infants query)
   :infants_on_lap (:passenger_infants_lap query)
   :stops (:stops query)
   :bags (:bags query)
   :outbound_date (:origin_date_start query)
   :return_date (:destiny_date_end query)})
