(defproject volare "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [telegrambot-lib "2.15.0"]
                 [cheshire "5.13.0"]
                 [environ "1.2.0"]
                 [org.clojure/tools.logging "1.3.0"]
                 [com.github.seancorfield/next.jdbc "1.3.994"]
                 [org.xerial/sqlite-jdbc "3.49.0.0"]
                 [clj-http "3.13.0"]]
  :jvm-opts ["-Dclojure.tools.logging.factory=clojure.tools.logging.impl/slf4j-factory"])
