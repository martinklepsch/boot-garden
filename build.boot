(set-env!
  :source-paths #{"src"}
  :dependencies '[[org.clojure/clojure       "1.6.0"       :scope "provided"]
                  ;; uncomment or remove when -rc1 is out
                  ;; [boot/core                 "2.0.0-rc1" :scope "provided"]
                  [ns-tracker                "0.2.2"]
                  [adzerk/bootlaces          "0.1.5"       :scope "test"]])

(require '[adzerk.bootlaces :refer :all])

(def +version+ "1.2.5")

(bootlaces! +version+)

(deftask add-src []
  (with-pre-wrap fileset
    (-> (reduce
          add-resource
          fileset
          (input-dirs fileset))
        commit!)))

(task-options!
 pom  {:project     'boot-garden
       :version     +version+
       :description "Boot task to compile Garden stylesheets to CSS."
       :url         "https://github.com/martinklepsch/boot-garden"
       :scm         {:url "https://github.com/martinklepsch/boot-garden"}
       :license     {:name "Eclipse Public License"
                     :url  "http://www.eclipse.org/legal/epl-v10.html"}})
