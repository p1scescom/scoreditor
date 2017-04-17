(ns scoreditor.core
  (:require [scoreditor.application :as app])
  (:gen-class
    :name scoreditor.core
    :main true))

(defn -main [& args]
  (let [stage (app/start-fx)]
    (doto stage
      (.setTitle "Epro2016 ScorEditor ")))
  #_(println "Finish"))
