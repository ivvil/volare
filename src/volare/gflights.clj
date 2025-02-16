(ns volare.gflights
  (:require [clj-http.client :as client]
            [clojure.string :as str]))

(def api-baseurl "https://serpapi.com/search.json")

(defn build-db-query
  [query api-key]
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
   :return_date (:destiny_date_end query)
   :api-key api-key
   :engine "google_flights"})

(defn run-query
  [query]
  (client/get api-baseurl {:accept :json :query-params query :as :reader}))
