(ns boot-garden.core
  {:boot/export-tasks true}
  (:require [clojure.java.io   :as io]
            [boot.core         :as boot :refer [deftask]]
            [boot.pod          :as pod]
            [boot.file         :as file]
            [boot.util         :as util]))

(def initial
  (atom true))

(defn add-dep [env dep]
  (update-in env [:dependencies] (fnil conj []) dep))

(defn ns-tracker-pod []
  (pod/make-pod (add-dep (boot/get-env) '[ns-tracker "0.2.2"])))

(defn garden-pool []
  (pod/pod-pool (add-dep (boot/get-env)) '[garden "1.2.5"]))

(deftask garden
  "compile garden"
  [o output-to PATH      str   "The output css file path relative to docroot."
   s styles-var SYM      sym   "The var containing garden rules"
   p pretty-print        bool  "Pretty print compiled CSS"
   v vendors             [str] "Vendors to apply prefixed for"
   a auto-prefix         [str] "Properties to auto-prefix with vendor-prefixes"]

  (let [output-path (or output-to "main.css")
        css-var     styles-var
        ns-sym      (symbol (namespace css-var))
        tmp         (boot/temp-dir!)
        out         (io/file tmp output-path)
        src-paths   (vec (boot/get-env :source-paths))
        garden-pods (garden-pool)
        ns-pod      (ns-tracker-pod)
        _           (pod/require-in ns-pod 'ns-tracker.core)
        _           (pod/with-eval-in ns-pod (def cns (ns-tracker.core/ns-tracker ~src-paths)))]
    (boot/with-pre-wrap fileset
      (when (or @initial (some #{ns-sym} (pod/with-eval-in ns-pod (cns))))
        (let [c-pod   (garden-pods :refresh)]
          (if @initial (reset! initial false))
          (util/info "Compiling %s...\n" (.getName out))
          (io/make-parents out)
          (pod/require-in c-pod 'garden.core)
          (pod/require-in c-pod (str ns-sym))
          (pod/with-call-in c-pod (garden.core/css {:output-to ~(.getPath out)
                                                    :pretty-print ~pretty-print
                                                    :vendors ~vendors
                                                    :auto-prefix ~(set auto-prefix)} ~css-var))))
      (-> fileset (boot/add-resource tmp) boot/commit!))))
