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

(defn garden-pod []
  (pod/make-pod (update-in (boot/get-env) [:dependencies] conj '[garden "1.2.5"])))

(deftask garden
  "compile garden"
  [o output-to PATH      str   "The output css file path relative to docroot."
   s styles-var SYM      sym   "The var containing garden rules"
   p pretty-print        bool  "Pretty print compiled CSS"
   v vendors NAME        [str] "Vendors to apply prefixed for"
   a auto-prefix NAME    [str] "Properties to auto-prefix with vendor-prefixes"]

  (util/info "TESTING")
  (let [output-path (or output-to "main.css")
        css-var     styles-var
        ns-sym      (symbol (namespace css-var))
        tgt-dir     (boot/temp-dir!)
        out         (io/file tgt-dir output-path)
        src-paths   (vec (boot/get-env :src-paths))
        ns-pod      (ns-tracker-pod)
        _           (.require ns-pod "ns-tracker.core")
        _           (pod/with-eval-in ns-pod (def cns (ns-tracker.core/ns-tracker ~src-paths)))]
    (boot/with-pre-wrap fileset
      (when (or @initial (some #{ns-sym} (pod/with-eval-in ns-pod (cns))))
        (let [c-pod   (garden-pod)]
          (if @initial (reset! initial false))
          (util/info "Compiling %s...\n" (.getName out))
          (io/make-parents out)
          (.require c-pod "garden.core")
          (.require c-pod (str ns-sym))
          (pod/with-call-in c-pod (garden.core/css {:output-to ~(.getPath out)
                                                    :pretty-print ~pretty-print
                                                    :vendors ~vendors
                                                    :auto-prefix ~(set auto-prefix)} ~css-var)))))))
