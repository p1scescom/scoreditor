(defproject scoreditor "1.0.3"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :profiles {:uberjar {:aot :all}}
  :main scoreditor.core
  :java-source-paths ["java"]
  :aot [scoreditor.application
        scoreditor.core]
  :dependencies [[org.clojure/clojure "1.8.0"]])
