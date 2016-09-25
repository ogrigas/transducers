(ns reducing-functions
  (:require [clojure.string :refer [upper-case]]))


(def names ["Han Solo"
            "Chewbacca"
            "Luc Skywalker"
            "R2D2"
            "Leia Organa"
            "Darth Vader"
            "C3P0"
            "Obi-Wan Kenobi"
            "Emperor Palpatine"
            "Lando Calrissian"])


;; collecting items

(into (sorted-set) names)

(reduce conj
        (sorted-set)
        names)

(reduce str
        ""
        names)


;; mapping items

(map upper-case names)

(reduce (fn [result name]
          (conj result (upper-case name)))
        []
        names)


;; fitering items

(filter #(.startsWith % "L") names)

(reduce (fn [result name]
          (if (.startsWith name "L") (conj result name) result))
        []
        names)
