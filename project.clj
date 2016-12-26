(defproject baseball "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repositories {"my.datomic.com"
                 {:url "https://my.datomic.com/repo"
                  :username "bob.erb@gmail.com"
                  :password "049f9a43-81bc-40cc-a4db-3b871a0b4839"}}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 ;; [korma "0.3.0-RC5"]
                 ;; [org.postgresql/postgresql "9.2-1002-jdbc4"]
                 ;; [com.datomic/datomic-pro "0.9.4724"]
                 [clj-glob "1.0.0"]]
  :main ^:skip-aot baseball.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
