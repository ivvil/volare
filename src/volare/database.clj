(ns volare.database
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [next.jdbc.result-set :as rs]
            [clojure.tools.logging :as log]))

(def db-spec {:dbtype "sqlite" :dbname "volare.db"})

(def ds (jdbc/get-datasource db-spec))

(defn foreign-keys
  []
  (jdbc/execute! ds ["PRAGMA foreign_keys = ON"]))

(defn- create-table-package-flights! []
  )

(defn create-tables!
  []
  (jdbc/with-transaction [tx ds]
    (create-table-users!)
    (create-table-user-queries!)
    (create-table-flights!)
    (create-table-layovers!)
    (create-table-packages!)
    (create-table-package-flights!)
    (create-table-package-layovers!)
    (create-table-package-prices!)))

(defn create-table-users!
  []
  (jdbc/execute! ds
                 ["CREATE TABLE IF NOT EXISTS users (
                     uid INTEGER PRIMARY KEY,
                     chat_id INTEGER NOT NULL)"]))

(defn create-table-user-queries!
  []
  (jdbc/execute! ds
                 ["CREATE TABLE IF NOT EXISTS user_queries (
                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                     uid INTEGER NOT NULL,
                     origin varchar(64) NOT NULL,
                     origin_date_start DATETIME NOT NULL,
                     origin_date_end DATETIME NOT NULL,
                     destiny varchar(64) NOT NULL,
                     destiny_date_start DATETIME NOT NULL,
                     destiny_date_end DATETIME NOT NULL,
                     class INTEGER NOT NULL,
                     passengers_adults INTEGER NOT NULL,
                     passenger_children INTEGER NOT NULL,
                     passenger_infants INTEGER NOT NULL,
                     passenger_infants_lap INTEGER NOT NULL,
                     stops INTEGER NOT NULL,
                     bags INTEGER NOT NULL,                     
                     FOREIGN KEY(uid) REFERENCES users(uid))"]))

(defn create-table-flights!
  []
  (jdbc/execute! ds
                 ["CREATE TABLE IF NOT EXISTS flights (
                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                     flight_number varchar(50) NOT NULL,
                     departure_airport_id varchar(10) NOT NULL,
                     arrival_airport_id varchar(10) NOT NULL,
                     departure_date DATETIME NOT NULL,
                     arrival_date DATETIME NOT NULL,
                     airline varchar(256) NOT NULL,
                     duration INTEGER NOT NULL,
                     airplane varchar(256),
                     travel_class varchar(50),
                     legroom varchar(50),
                     overnight BOOLEAN,
                     often_delayed BOOLEAN,
                     FOREIGN KEY(airline) REFERENCES airlines(name),
                     FOREIGN KEY(departure_airport_id) REFERENCES airports(id),
                     FOREIGN KEY(arrival_airport_id) REFERENCES airports(id))"]))

(defn create-table-layovers!
  []
  (jdbc/execute! ds
                 ["CREATE TABLE IF NOT EXISTS layovers (
                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                     airport_id varchar(10) NOT NULL,
                     duration INTEGER NOT NULL,
                     overnight BOOLEAN,
                     FOREIGN KEY(airport_id) REFERENCES airports(id))"]))

(defn create-table-packages!
  []
  (jdbc/execute! ds
                 ["CREATE TABLE IF NOT EXISTS packages (
                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                     total_duration INTEGER NOT NULL,
                     type varchar(50),
                     carbon_emissions_this_flight INTEGER,
                     carbon_emissions_typical INTEGER,
                     carbon_emissions_difference_percent INTEGER,
                     booking_token varchar(256),
                     departure_token varchar(256))"]))

(defn create-table-package-flights!
  []
  (jdbc/execute! ds
                 ["CREATE TABLE IF NOT EXISTS package_flights (
                     package_id INTEGER NOT NULL,
                     flight_id INTEGER NOT NULL,
                     sequence INTEGER NOT NULL,
                     FOREIGN KEY(package_id) REFERENCES packages(id),
                     FOREIGN KEY(flight_id) REFERENCES flights(id),
                     PRIMARY KEY(package_id, flight_id))"]))

(defn create-table-package-layovers!
  []
  (jdbc/execute! ds
                 ["CREATE TABLE IF NOT EXISTS package_layovers (
                     package_id INTEGER NOT NULL,
                     layover_id INTEGER NOT NULL,
                     sequence INTEGER NOT NULL,
                     FOREIGN KEY(package_id) REFERENCES packages(id),
                     FOREIGN KEY(layover_id) REFERENCES layovers(id),
                     PRIMARY KEY(package_id, layover_id))"]))

(defn create-table-package-prices!
  []
  (jdbc/execute! ds
                 ["CREATE TABLE IF NOT EXISTS package_prices (
                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                     package_id INTEGER NOT NULL,
                     price INTEGER NOT NULL,
                     best_flight BOOLEAN,
                     recorded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                     FOREIGN KEY(package_id) REFERENCES packages(id))"]))

(defn insert-user
  [uid chat_id]
  (try
    (sql/insert! ds :users {:uid uid :chat_id chat_id})
    (catch Exception e
      (log/error "Error inserting user:" e))))

(defn get-user-chatid
  [uid]
  (try
    (let [res (sql/query ds
                          ["SELECT * FROM users WHERE uid = ?" uid]
                          {:builder-fn rs/as-unqualified-lower-maps})]
      (-> (first res)
          :chat_id))
    (catch Exception e
      (log/error "Error retrieving user chat ID:" e)
      nil)))


(defn insert-query
  [uid origin origin-date-start origin-date-end destiny destiny-date-start destiny-date-end class passengers-adults passengers-children passengers-infants passengers-infants-lap stops bags]
  (try
    (sql/insert! ds :user_queries {:uid uid
                                   :origin origin
                                   :origin_date_start origin-date-start
                                   :origin_date_end origin-date-end
                                   :destiny destiny
                                   :destiny_date_start destiny-date-start
                                   :destiny_date_end destiny-date-end
                                   :class class
                                   :passengers_adults passengers-adults
                                   :passengers_children passengers-children
                                   :passengers_infants passengers-infants
                                   :passengers_infants_lap passengers-infants-lap
                                   :stops stops
                                   :bags bags})
    (catch Exception e
      (log/error "Error inserting query:" e))))

(defn get-user-querys
  [uid]
  (sql/query ds
             ["SELECT * FROM user_queries WHERE uid = ?" uid]
             {:builder-fn rs/as-unqualified-lower-maps}))

(defn insert-flight
  [flight-number departure-airport-id arrival-airport-id departure-date arrival-date airline duration airplane travel-class legroom overnight often-delayed]
  (sql/insert! ds :flights
               {:flight_number flight-number
                :departure_airport_id departure-airport-id
                :arrival_airport_id arrival-airport-id
                :departure_date departure-date
                :arrival_date arrival-date
                :airline airline
                :duration duration
                :airplane airplane
                :travel_class travel-class
                :legroom legroom
                :overnight overnight
                :often_delayed often-delayed}))

(defn get-flight
  [flight-id]
  (sql/query ds
             ["SELECT * FROM flights WHERE id = ?" flight-id]
             {:builder-fn rs/as-unqualified-lower-maps}))

(defn insert-layover
  [airport-id duration overnight]
  (sql/insert! ds :layovers
               {:airport_id airport-id
                :duration duration
                :overnight overnight}))

(defn get-layover
  [layover-id]
  (sql/query ds
             ["SELECT * FROM layovers WHERE id = ?" layover-id]
             {:builder-fn rs/as-unqualified-lower-maps}))

(defn insert-package
  [total-duration type carbon-emissions-this-flight carbon-emissions-typical carbon-emissions-difference-percent booking-token departure-token]
  (sql/insert! ds :packages
               {:total_duration total-duration
                :type type
                :carbon_emissions_this_flight carbon-emissions-this-flight
                :carbon_emissions_typical carbon-emissions-typical
                :carbon_emissions_difference_percent carbon-emissions-difference-percent
                :booking_token booking-token
                :departure_token departure-token}))

(defn get-package
  [package-id]
  (sql/query ds
             ["SELECT * FROM packages WHERE id = ?" package-id]
             {:builder-fn rs/as-unqualified-lower-maps}))

(defn insert-package-flight
  [package-id flight-id sequence]
  (sql/insert! ds :package_flights
               {:package_id package-id
                :flight_id flight-id
                :sequence sequence}))

(defn get-package-flights
  [package-id]
  (sql/query ds
             ["SELECT * FROM package_flights WHERE package_id = ?" package-id]
             {:builder-fn rs/as-unqualified-lower-maps}))

(defn insert-package-layover
  [package-id layover-id sequence]
  (sql/insert! ds :package_layovers
               {:package_id package-id
                :layover_id layover-id
                :sequence sequence}))

(defn get-package-layovers
  [package-id]
  (sql/query ds
             ["SELECT * FROM package_layovers WHERE package_id = ?" package-id]
             {:builder-fn rs/as-unqualified-lower-maps}))

(defn insert-package-price
  [package-id price best-flight]
  (sql/insert! ds :package_prices
               {:package_id package-id
                :price price
                :best_flight best-flight}))

(defn get-package-prices
  [package-id]
  (sql/query ds
             ["SELECT * FROM package_prices WHERE package_id = ?" package-id]
             {:builder-fn rs/as-unqualified-lower-maps}))



