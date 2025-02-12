(ns volare.telegram
  (:require [telegrambot-lib.core :as tbot])
  (:require '[environ.boot :refer [env]]))

(def bot-api-token
  (env :bot-api-token))
