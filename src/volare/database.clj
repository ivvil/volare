(ns volare.database
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [next.jdbc.result-set :as rs]))

(def db-spec {:dbtype "sqlite" :dbname "volare.db"})

(def ds (jdbc/get-datasource db-spec))

(defn foreign-keys
  []
  (jdbc/execute! ds ["PRAGMA foreign_keys = ON"]))

(defn create-table-users!
  []
  (jdbc/execute! ds
                 ["CREATE TABLE IF NOT EXISTS users (
                     uid INTEGER PRIMARY KEY,
                     chat_id INTEGER NOT NULL)"]))

(defn create-table-airports!
  []
  (jdbc/execute! ds
                 ["CREATE TABLE IF NOT EXISTS airports (
                     id varchar(10) PRIMARY KEY NOT NULL,
                     name varchar(255) NOT NULL)"]))

(defn create-table-users-queries!
  []
  (jdbc/execute! ds
                 ["CREATE TABLE IF NOT EXISTS user_queries (
                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                     uid INTEGER NOT NULL,
                     origin varchar(10) NOT NULL,
                     origin_date_start DATETIME NOT NULL,
                     origin_date_end DATETIME NOT NULL,
                     destiny varchar(10) NOT NULL,
                     destiny_date_start DATETIME NOT NULL,
                     destiny_date_end DATETIME NOT NULL,
                     FOREIGN KEY(uid) REFERENCES users(uid))"]))

;; (defn create-table-vuelos!
;;   []
;;   (jdbc/execute! ds
;;                  ["CREATE TABLE IF NOT EXISTS vuelos (
;;                      departure_airport_id varchar(10) NOT NULL PRIMARY KEY,
;;                      uid INTEGER NOT NULL,
;;                      origin varchar(10) NOT NULL,
;;                      origin_date_start DATETIME NOT NULL,
;;                      origin_date_end DATETIME NOT NULL,
;;                      destiny varchar(10) NOT NULL,
;;                      destiny_date_start DATETIME NOT NULL,
;;                      destiny_date_end DATETIME NOT NULL,
;;                      FOREIGN KEY(uid) REFERENCES users(uid))"]))

(defn insert-user
  [uid chat_id]
  (sql/insert! ds :users {:uid uid :chat_id chat_id}))

(defn get-user-chatid
  [uid]
  (let [res (sql/query ds
             ["SELECT * FROM users WHERE uid = ?" uid]
             {:builder-fn rs/as-unqualified-lower-maps})]
    (-> (first res)
        :chat_id)))

(defn insert-query
  [uid origin origin-date-start origin-date-end destiny destiny-date-start destiny-date-end]
  (sql/insert! ds :user_queries {:uid uid
                                 :origin origin
                                 :origin_date_start origin-date-start
                                 :origin_date_end origin-date-end
                                 :destiny destiny
                                 :destiny_date_start destiny-date-start
                                 :destiny_date_end destiny-date-end}))

(defn get-user-querys
  [uid]
  (sql/query ds
             ["SELECT * FROM user_queries WHERE uid = ?" uid]
             {:builder-fn rs/as-unqualified-lower-maps}))

