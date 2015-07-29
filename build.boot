(set-env!
  :source-paths #{"src"}
  :dependencies '[[org.clojure/clojure       "1.6.0"  :scope "provided"]
                  [adzerk/bootlaces          "0.1.11" :scope "test"]])

(require '[adzerk.bootlaces :refer [bootlaces! build-jar push-release]])

(def +version+ "1.2.5-6")

(bootlaces! +version+)

(deftask add-src []
  (with-pre-wrap fileset
    (-> (reduce
          add-resource
          fileset
          (input-dirs fileset))
        commit!)))

(task-options!
 pom  {:project     'org.martinklepsch/boot-garden
       :version     +version+
       :description "Boot task to compile Garden stylesheets to CSS."
       :url         "https://github.com/martinklepsch/boot-garden"
       :scm         {:url "https://github.com/martinklepsch/boot-garden"}
       :license     {"EPL" "http://www.eclipse.org/legal/epl-v10.html"}})
