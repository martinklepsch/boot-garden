(set-env!
 :source-paths #{"src"}
 :dependencies '[[org.martinklepsch/boot-garden "1.2.5-4"]])

(require '[org.martinklepsch.boot-garden :refer [garden]])

(task-options! garden {:styles-var 'stylesheet/combined
                       :output-to "public/css/garden.css"})
