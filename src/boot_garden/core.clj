(ns boot-garden.core
  {:boot/export-tasks true}
  (:require [clojure.java.io   :as io]
            [boot.core         :as boot :refer [deftask]]
            [boot.pod          :as pod]
            [boot.file         :as file]
            [boot.util         :as util]))

(def initial
  (atom true))

(defn ns-tracker-pod []
  (pod/make-pod (assoc-in (boot/get-env) [:dependencies] '[[ns-tracker "0.2.2"]])))

(deftask garden
  "compile garden"
  [o output-to PATH      str   "The output css file path relative to docroot."
   s styles-var SYM      sym   "The var containing garden rules"
   p pretty-print        bool  "Pretty print compiled CSS"
   v vendors NAME        [str] "Vendors to apply prefixed for"
   a auto-prefix NAME    [str] "Properties to auto-prefix with vendor-prefixes"]

  (let [output-path (or output-to "main.css")
        css-var     styles-var
        ns-sym      (symbol (namespace css-var))
        tgt-dir     (boot/resource-dir!)
        out         (io/file tgt-dir output-path)
        src-paths   (vec (boot/get-env :src-paths))
        ns-pod      (ns-tracker-pod)
        _           (pod/require-in ns-pod 'ns-tracker.core)
        _           (pod/eval-in ns-pod (def cns (ns-tracker.core/ns-tracker ~src-paths)))]
    (boot/with-pre-wrap
      (when (or @initial (some #{ns-sym} (pod/eval-in ns-pod (cns))))
        (let [c-pod   (pod/make-pod (boot/get-env))]
          (if @initial (reset! initial false))
          (util/info "Compiling %s...\n" (.getName out))
          (io/make-parents out)
          (pod/require-in c-pod 'garden.core)
          (pod/require-in c-pod (str ns-sym))
          (pod/eval-in c-pod (garden.core/css {:output-to ~(.getPath out)
                                               :pretty-print ~pretty-print
                                               :vendors ~vendors
                                               :auto-prefix ~(set auto-prefix)} ~css-var)))))))
