(ns stylesheet
  (:require [included :as incl]
            [garden.def :as gdn]))

(gdn/defstyles screen
  [:body
   {:font-family "Helvetica Neue"
    :font-size   "16px"
    :line-height 1.5}])

(gdn/defstyles combined
  screen
  incl/links)
