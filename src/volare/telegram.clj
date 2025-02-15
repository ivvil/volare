(ns volare.telegram
  (:require [telegrambot-lib.core :as tbot]
            [clojure.tools.logging :as log]))

(def conf
  {:sleep 10000})

(def commands
  {:start {:description "Start session"
           :handler (fn [bot msj _args]
                      (let [chat-id (get-in msj [:message :chat :id])]
                        (tbot/send-message bot chat-id "big chungus")))}})

(defn- register-commands [bot]
  (let [command-list (map (fn [[cmd {:keys [description]}]] 
                            {:command (name cmd) :description description})
                          commands)]
    (tbot/set-my-commands bot {:commands command-list})))

(defn poll-updates
  ([bot]
   (poll-updates bot nil))

  ([bot offset]
   (let [resp (tbot/get-updates bot {:offset offset
                                     :timeout (:timeout conf)})]
     (if (contains? resp :error)
       (log/error "tbot/get-updates error:" (:error resp))
       resp))))

(defonce update-id (atom nil))

(defn set-id!
  "Sets the update id to process next as the the passed in `id`."
  [id]
  (reset! update-id id))

(defn handle-msg
  [bot msg]
  (let [chat-id (get-in msg [:message :chat :id])
        text (get-in msg [:message :text])]
    (log/info "Received message:" text)
    (tbot/send-message bot chat-id (str "You said: " text))))

(defn run-bot
  "Process messages"
  [bot]
  (log/info "Bot process started.")

  (loop []
    (log/info "Checking for bot updates")
    (let [updates (poll-updates bot @update-id)
          messages (:result updates)]
      (doseq [msg messages]
        (handle-msg bot msg)

        (-> msg
            :update_id
            inc
            set-id!))
      (Thread/sleep (:sleep conf)))
    (recur)))

(defn -main
  []
  (let [bot (tbot/create "key")]
    (register-commands bot)
    (run-bot bot)))
