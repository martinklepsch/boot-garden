(set-env!
 :src-paths    #{"src"}
 :dependencies '[[boot-garden         "1.2.5"]
                 [org.clojure/clojure "1.6.0"       :scope "provided"]
                 [boot/core           "2.0.0-pre28" :scope "provided"]])

(require
 '[boot-garden.core :refer [garden]])

(core/task-options! garden [:styles-var 'stylesheet/screen
                            :output-to "public/css/garden.css"])
