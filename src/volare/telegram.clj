(ns volare.telegram
  (:require [telegrambot-lib.core :as tbot]
            [environ.core :refer [env]]))

(def bot-api-token (env :bot-api-token))
