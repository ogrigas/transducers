(ns transducers
  (:require [clojure.string :refer [upper-case]])
  (:require [clojure.core.async :as async :refer [go <! >! <!! >!!]])
  (:require [rx.lang.clojure.core :as rx])
  (:import (rx Observable)))


(def star-wars
     [{:name "Han Solo" :human? true}
      {:name "Chewbacca" :human? false}
      {:name "Luc Skywalker" :human? true}
      {:name "R2D2" :human? false}
      {:name "Leia Organa" :human? true}
      {:name "Darth Vader" :human? true}
      {:name "C3P0" :human? false}
      {:name "Obi-Wan Kenobi" :human? true}
      {:name "Emperor Palpatine" :human? true}
      {:name "Lando Calrissian" :human? true}])


(comment

  ;; transforming a collection

  (->> star-wars

       (filter :human?)
       (map :name)
       (map upper-case)
       (distinct)
       (take 5)

       (into []))


  ;; transforming a stream

  (->> (Observable/from ^Iterable star-wars)

       (rx/filter :human?)
       (rx/map :name)
       (rx/map upper-case)
       (rx/distinct)
       (rx/take 5)

       (.toList)
       (.toBlocking)
       (.single))


  ;; transforming a channel

  (->> (async/to-chan star-wars)

       (async/filter< :human?)
       (async/map< :name)
       (async/map< upper-case)
       (async/unique)
       (async/take 5)

       (async/into [])
       (async/<!!))


  ;; extract all transformations as a reusable transducer

  (def racify
       (comp (filter :human?)
             (map :name)
             (map upper-case)
             (distinct)
             (take 5)))


  ;; using transducer on a collection

  (sequence racify star-wars)
  (into (sorted-set) racify star-wars)
  (transduce racify conj star-wars)
  (transduce racify str star-wars)
  (transduce (comp racify (interpose " :: ")) str star-wars)
  (transduce (comp racify (map count)) + star-wars)


  ;; using transducer on a channel

  (def racist-chan (async/chan 1 racify))
  (async/onto-chan racist-chan star-wars)
  (go (println (<! racist-chan)))


  ;; custom transducers

  (defn trace [rf]
    (fn
      ([] (rf))
      ([result] (rf result))
      ([result input] (println "input:" input) (rf result input))))

  (sequence (comp trace racify trace) star-wars))
