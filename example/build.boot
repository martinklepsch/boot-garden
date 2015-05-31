(set-env!
 :source-paths #{"src"}
 :dependencies '[[org.martinklepsch/boot-garden "1.2.5-3"]
                 [org.clojure/clojure "1.6.0" :scope "provided"]
                 [boot/core           "2.0.0" :scope "provided"]])

(require '[org.martinklepsch.boot-garden :refer [garden]])

(task-options! garden {:styles-var 'stylesheet/combined
                       :output-to "public/css/garden.css"})
