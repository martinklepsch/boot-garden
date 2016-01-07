(set-env!
 :source-paths #{"src"}
 :dependencies '[[org.martinklepsch/boot-garden "1.3.0"]
                 [org.clojure/data.json "0.2.6"]])

(require '[org.martinklepsch.boot-garden :refer [garden]])

(task-options! garden {:styles-var   'stylesheet/combined
                       :output-to    "public/css/garden.css"
                       :pretty-print false})
