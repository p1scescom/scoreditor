(defproject scoreditor "1.0.3"
  :description "Score editor for Pastell"
  :url "https://github.com/p1scescom/scoreditor"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"
            :year 2017
            :key "mit"}
  :profiles {:uberjar {:aot :all}}
  :main scoreditor.core
  :java-source-paths ["java"]
  :aot [scoreditor.application
        scoreditor.core]
  :dependencies [[org.clojure/clojure "1.8.0"]])
