(ns html-soup.examples
  (:require html-soup.core
            dynadoc.core)
  (:require-macros [dynadoc.example :refer [defexample]]))

(defexample html-soup.core/code->html
  (code->html "(+ 1 2)"))

(defexample html-soup.core/code->hiccup
  (code->hiccup "(+ 1 2)"))

