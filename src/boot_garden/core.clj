(ns boot-garden.core
  {:boot/export-tasks true}
  (:require [clojure.java.io   :as io]
            [boot.pod          :as pod]
            [boot.core         :as core]
            [boot.file         :as file]
            [boot.util         :as util]))

(deftask garden
  "compile garden"
  [o output-to PATH      str   "The output css file path relative to docroot."
   s styles-var SYM      sym   "The var containing garden rules"
   p pretty-print        bool  "Pretty print compiled CSS"
   v vendors NAME        [str] "Vendors to apply prefixed for"
   a auto-prefix NAME    [str] "Properties to auto-prefix with vendor-prefixes"]

  (let [output-path (or output-to "main.css")
        css-var     styles-var
        tgt-dir     (core/mktgtdir!)
        out         (io/file tgt-dir output-path)]
    (with-pre-wrap
      (let [w (pod/make-pod (core/get-env))]
        (util/info "Compiling %s ...\n" (.getName out))
        (pod/require-in-pod w "garden.core")
        (pod/require-in-pod w (namespace css-var))
        (pod/eval-in w (garden.core/css {:output-to ~(.getPath out)
                                         :pretty-print ~pretty-print
                                         :vendors ~vendors
                                         :auto-prefix ~(set auto-prefix)} ~css-var))))))
