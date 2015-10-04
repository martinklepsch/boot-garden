(ns stylesheet
  (:require [included :as incl]
            [clojure.data.json :as json]
            [garden.def :as gdn]))

(def x (json/write-str {:a 1}))

(gdn/defstyles screen
  [:body
   {:font-family "Helvetica Neue"
    :font-size   "16px"
    :line-height 1.5}])

(gdn/defstyles combined
  screen
  incl/links)
