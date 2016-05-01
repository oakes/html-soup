(ns html-soup.core
  (:require [clojure.string :as str]
            [schema.core :refer [maybe either Any Str Int Keyword Bool]
             #?@(:clj [:as s])]
            [tag-soup.core :as ts]
            #?(:cljs
               [goog.string :as gstring :refer [format]]))
  #?(:cljs (:require-macros [schema.core :as s])))

(s/defn split-lines :- [Str]
  "Splits the string into lines."
  [s :- Str]
  (vec (.split s "\n" -1)))

(s/defn escape-html-str :- Str
  "Escapes an HTML string"
  [s :- Str]
  (str/escape s
    {\< "&lt;"
     \> "&gt;"
     \& "&amp;"
     \" "&quot;"
     \' "&apos;"}))

(s/defn escape-html-char :- Str
  "Escapes an HTML character"
  [s :- Str]
  (case s
    \< "&lt;"
    \> "&gt;"
    \& "&amp;"
    \" "&quot;"
    \' "&apos;"
    s))

(s/defn tag->html :- Str
  "Returns an HTML string for the given tag description."
  [tag :- {Keyword Any}]
  (cond
    (:delimiter? tag) "<span class='delimiter'>"
    (:error? tag) (format
                    "<span class='error' data-message='%s'></span>"
                    (some-> (:message tag) escape-html-str))
    (:line tag) (let [value (:value tag)]
                  (cond
                    (symbol? value) "<span class='symbol'>"
                    (list? value) "<span class='collection list'>"
                    (vector? value) "<span class='collection vector'>"
                    (map? value) "<span class='collection map'>"
                    (set? value) "<span class='collection set'>"
                    (number? value) "<span class='number'>"
                    (string? value) "<span class='string'>"
                    (keyword? value) "<span class='keyword'>"
                    (nil? value) "<span class='nil'>"
                    (contains? #{true false} value) "<span class='boolean'>"
                    :else "<span>"))
    (:end-line tag) "</span>"
    :else "<span>"))

(s/defn line->html :- Str
  "Returns the given line with html added."
  [line :- Str
   tags-for-line :- [{Keyword Any}]]
  (let [tags-for-line (sort-by ts/get-column tags-for-line)
        html-per-column (sequence (comp (partition-by ts/get-column)
                                        (map #(str/join (map tag->html %))))
                                  tags-for-line)
        columns (set (map ts/get-column tags-for-line))
        segments (loop [i 0
                        segments (transient [])
                        current-segment (transient [])]
                   (if-let [c (some-> line (get i) escape-html-char)]
                     (if (contains? columns (inc i))
                       (recur (inc i)
                              (conj! segments (persistent! current-segment))
                              (transient [c]))
                       (recur (inc i)
                              segments
                              (conj! current-segment c)))
                     (->> (persistent! current-segment)
                          (conj! segments)
                          persistent!
                          (map str/join))))]
    (str/join (interleave segments (concat html-per-column (repeat ""))))))

(s/defn lines->html :- [Str]
  "Returns the lines with html added."
  [lines :- [Str]
   tags :- [{Keyword Any}]]
  (let [tags-by-line (group-by ts/get-line tags)]
    (sequence (comp (partition-all 2)
                    (map (fn [[i line]]
                           (line->html line (get tags-by-line (inc i))))))
              (interleave (iterate inc 0) lines))))

(s/defn code->html :- Str
  "Returns the code in the given string with html added."
  [code :- Str]
  (let [lines (split-lines code)
        tags (ts/str->tags code)]
    (str/join \newline (lines->html lines tags))))
