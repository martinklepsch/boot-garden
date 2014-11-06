# `boot-garden`

Boot task to compile Garden stylesheets.

Provides the `garden` task, which compiles Garden to CSS.

[![Clojars Project](http://clojars.org/boot-garden/latest-version.svg)](http://clojars.org/boot-garden)

## Usage

Lets assume you have a `styles.clj` as follows:

```clojure
(ns my-project.styles
  (:require [garden.def :refer [defrule defstyles]]
            [garden.stylesheet :refer [rule]]))

(defstyles base
  (let [body (rule :body)]
    (body
     {:font-family "Helvetica Neue"
      :font-size   "16px"
      :line-height 1.5})))
```

### Terminal

In a terminal you can compile any `defstyles`-defined stylesheet as follows:

```
boot garden -s my-project.styles/base
```

To regenerate stylesheets on changes you can use boot's generic `watch` task:

```
boot watch garden -s my-project.styles/base
```

### build.boot file in your project

In your `build.boot` you could call it like this:

```clojure
(deftask run
  "Generate CSS from Garden and watch for future changes"
  []
  (comp (watch) (garden :styles-var 'creationist.styles/screen)))
```

## Options

See the [boot project](https://github.com/boot-clj/boot) for more information
on how to use these. By default `boot-garden` will save the compiled CSS file at
`target/main.css`.

```clojure
[o output-to PATH      str   "The output css file path relative to target/"
 s styles-var SYM      sym   "The var containing garden rules"
 p pretty-print        bool  "Pretty print compiled CSS"
 v vendors NAME        [str] "Vendors to apply prefixed for"
 a auto-prefix NAME    [str] "Properties to auto-prefix with vendor-prefixes"]
```
