(ns org.martinklepsch.boot-garden
  {:boot/export-tasks true}
  (:require [clojure.java.io   :as io]
            [boot.core         :as boot :refer [deftask]]
            [boot.pod          :as pod]
            [boot.file         :as file]
            [boot.util         :as util]))

(def processed
  (atom #{}))

(defn add-dep [env dep]
  (update-in env [:dependencies] (fnil conj []) dep))

(defn ns-tracker-pod []
  (->> '[[ns-tracker "0.3.0"] [org.clojure/tools.namespace "0.2.11"]]
       (assoc (boot/get-env) :dependencies)
       pod/make-pod))

(defonce garden-pods
  (pod/pod-pool (add-dep (boot/get-env) '[garden "1.3.2"])
                :init (fn [pod] (pod/require-in pod 'garden.core))))

(defn css-prepend-resources [out css-prepend]
  (with-open [os (io/output-stream out)]
    (doseq [p css-prepend]
      (if-let [p-resource (io/resource p)]
        (do (util/info "Prepending %s with %s...\n"
                       (.getName out) p)
            (io/copy (io/file p-resource) os))
        (util/warn "Prepend resource %s not found.\n" p)))))

(deftask garden
  "compile garden"
  [o output-to PATH      str   "The output css file path relative to docroot."
   s styles-var SYM      sym   "The var containing garden rules"
   p pretty-print        bool  "Pretty print compiled CSS"
   v vendors VENDORS     [str] "Vendors to apply prefixed for"
   c css-prepend PREPEND [str] "CSS resources to be prepended to output"
   a auto-prefix PREFIX  #{kw} "Properties to auto-prefix with vendor-prefixes"]

  (let [output-path (or output-to "main.css")
        css-var     styles-var
        ns-sym      (symbol (namespace css-var))
        tmp         (boot/tmp-dir!)
        out         (io/file tmp output-path)
        src-paths   (vec (boot/get-env :source-paths))
        ns-pod      (ns-tracker-pod)]
    (pod/with-eval-in ns-pod
      (require 'ns-tracker.core)
      (def cns (ns-tracker.core/ns-tracker ~src-paths)))
    (boot/with-pre-wrap fileset
      (let [initial (not (contains? @processed css-var))]
        (when (or initial (some #{ns-sym} (pod/with-eval-in ns-pod (cns))))
          (let [c-pod (garden-pods :refresh)]
            (when initial (swap! processed conj css-var))
            (util/info "Compiling %s...\n" (.getName out))
            (io/make-parents out)
            (when css-prepend
              (css-prepend-resources out css-prepend))
            (pod/with-eval-in c-pod
              (require '~ns-sym)
              (spit ~(.getPath out)
                    (garden.core/css {:pretty-print? ~pretty-print
                                      :vendors ~vendors
                                      :auto-prefix ~auto-prefix} ~css-var)
                    :append true)))))
      (-> fileset (boot/add-resource tmp) boot/commit!))))
