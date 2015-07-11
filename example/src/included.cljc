(ns included
  (:require 
   #?@(:clj  [[clojure.string :as str]
              [garden.def     :as gdn]]
       :cljs [[garden.def     :as gdn]])))

(gdn/defstyles links
  [:a {:color "blue"}])
