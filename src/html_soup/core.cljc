(ns html-soup.core
  (:require [clojure.string :as str]
            [parinferish.core :as ps]))

(defn escape-html-str
  "Escapes an HTML string. Mainly for internal use; you probably don't need to use this directly."
  [s]
  (str/escape s
    {\< "&lt;"
     \> "&gt;"
     \& "&amp;"
     \" "&quot;"
     \' "&apos;"}))

(defn node->html [[type & tokens :as node]]
  (let [error (-> node meta :error-message)
        inner-str (str
                    (str/join tokens)
                    (when error
                      (str "<span class='error' data-message='"
                           (escape-html-str error)
                           "'></span>")))]
    (if (-> node meta :whitespace?)
      inner-str
      (str "<span class='" (name type) "'>" inner-str "</span>"))))

(defn code->html
  "Returns the code in the given string with html added."
  [code]
  (->> (ps/parse code)
       (ps/flatten node->html)
       str/join))

(defn node->hiccup [[type & tokens :as node]]
  (let [error (-> node meta :error-message)]
    (if (-> node meta :whitespace?)
      (first tokens)
      (cond-> (into [:span {:class (name type)}] tokens)
              error
              (conj [:error error])))))

(defn code->hiccup
  "Returns the code in the given string with Hiccup-compatible data structures added."
  [code]
  (->> (ps/parse code)
       (ps/flatten node->hiccup)
       (into [:span {}])))

