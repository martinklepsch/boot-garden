(ns boot-garden.core
  {:boot/export-tasks true}
  (:require [clojure.java.io   :as io]
            [ns-tracker.core   :as nst]
            [boot.core         :as boot :refer [deftask]]
            [boot.pod          :as pod]
            [boot.file         :as file]
            [boot.util         :as util]))

(def initial
  (atom true))

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
        tgt-dir     (boot/mktgtdir!)
        out         (io/file tgt-dir output-path)
        changed-ns  (nst/ns-tracker (seq (boot/get-env :src-paths)))]
    (boot/with-pre-wrap
      (when (or @initial (some #{ns-sym} (changed-ns)))
        (let [w   (pod/make-pod (boot/get-env))]
          (if @initial (reset! initial false))
          (util/info "Compiling %s ...\n" (.getName out))
          (io/make-parents out)
          (pod/require-in-pod w "garden.core")
          (pod/require-in-pod w (str ns-sym))
          (pod/eval-in w (garden.core/css {:output-to ~(.getPath out)
                                           :pretty-print ~pretty-print
                                           :vendors ~vendors
                                           :auto-prefix ~(set auto-prefix)} ~css-var)))))))
