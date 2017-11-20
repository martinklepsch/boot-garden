(set-env!
 :source-paths #{"src"}
 :resource-paths #{"resources"}
 :dependencies '[[org.martinklepsch/boot-garden "1.3.2-1"]
                 [org.clojure/data.json "0.2.6"]])

(require '[org.martinklepsch.boot-garden :refer [garden]])

(task-options! garden {:styles-var   'stylesheet/combined
                       :output-to    "public/css/garden.css"
                       :css-prepend  ["css/prepend1.css" "css/prepend2.css"]
                       :pretty-print false})
