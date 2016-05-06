(ns html-soup.core
  (:require [clojure.string :as str]
            [schema.core :refer [maybe either Any Str Int Keyword Bool pred]
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
    (:end-line tag) "</span>"))

(s/defn tag->hiccup :- [Any]
  "Returns a Hiccup-compatible data structure for the given tag description."
  [tag :- {Keyword Any}]
  (cond
    (:delimiter? tag) [:span {:class "delimiter"}]
    (:error? tag) [:span {:class "error"
                          :data-message (some-> (:message tag) escape-html-str)}]
    (:line tag) (let [value (:value tag)]
                  (cond
                    (symbol? value) [:span {:class "symbol"}]
                    (list? value) [:span {:class "collection list"}]
                    (vector? value) [:span {:class "collection vector"}]
                    (map? value) [:span {:class "collection map"}]
                    (set? value) [:span {:class "collection set"}]
                    (number? value) [:span {:class "number"}]
                    (string? value) [:span {:class "string"}]
                    (keyword? value) [:span {:class "keyword"}]
                    (nil? value) [:span {:class "nil"}]
                    (contains? #{true false} value) [:span {:class "boolean"}]
                    :else [:span]))
    (:end-line tag) nil))

(def tags-for-line->html
  (comp
    (partition-by ts/get-column)
    (map #(str/join (map tag->html %)))))

(def tags-for-line->hiccup
  (comp
    (partition-by ts/get-column)
    (map #(map tag->hiccup %))))

(s/defn line->segments :- [Str]
  "Splits a line into segments where tags are supposed to appear."
  [line :- Str
   tags-for-line :- [{Keyword Any}]]
  (let [columns (set (map ts/get-column tags-for-line))]
    (loop [i 0
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
             (map str/join))))))

(s/defn line->html :- Str
  "Returns the given line with html added."
  [line :- Str
   tags-for-line :- [{Keyword Any}]]
  (let [html-per-column (sequence tags-for-line->html tags-for-line)
        segments (line->segments line tags-for-line)]
    (str/join (interleave segments (concat html-per-column (repeat ""))))))

(s/defn line->hiccup :- [Any]
  "Returns the given line with Hiccup-compatible data structures added."
  [line :- Str
   tags-for-line :- [{Keyword Any}]]
  (let [hiccup-per-column (sequence tags-for-line->hiccup tags-for-line)
        segments (map list (line->segments line tags-for-line))]
    (apply concat (interleave segments (concat hiccup-per-column (repeat nil))))))

(s/defn parse-lines :- [Any]
  "Returns the lines parsed with the given function."
  [parse-fn :- (pred fn?)
   lines :- [Str]
   tags :- [{Keyword Any}]]
  (let [tags-by-line (group-by ts/get-line tags)]
    (sequence (comp
                (partition-all 2)
                (map (fn [[i line]]
                       (let [tags-for-line (get tags-by-line (inc i))
                             tags-for-line (sort-by ts/get-column tags-for-line)]
                         (parse-fn line tags-for-line)))))
      (interleave (iterate inc 0) lines))))

(s/defn code->html :- Str
  "Returns the code in the given string with html added."
  [code :- Str]
  (let [lines (split-lines code)
        tags (ts/str->tags code)]
    (str/join \newline (parse-lines line->html lines tags))))

(s/defn structurize-hiccup :- [Any]
  "Takes a flat list of Hiccup-compatible data and adds structure to it."
  ([flat-hiccup :- [Any]]
   (second (structurize-hiccup flat-hiccup [])))
  ([flat-hiccup :- [Any]
    structured-hiccup :- [Any]]
   (loop [flat-hiccup flat-hiccup
          structured-hiccup structured-hiccup]
     (if-let [token (first flat-hiccup)]
       (cond
         (string? token)
         (recur (rest flat-hiccup) (conj structured-hiccup token))
         (vector? token)
         (let [[flat structured] (structurize-hiccup (rest flat-hiccup) token)]
           (recur flat (conj structured-hiccup structured))))
       [(rest flat-hiccup) structured-hiccup]))))

(s/defn code->hiccup :- [Any]
  "Returns the code in the given string with Hiccup-compatible data structures added."
  [code :- Str]
  (let [lines (split-lines code)
        tags (ts/str->tags code)
        hiccup (apply concat (parse-lines line->hiccup lines tags))]
    (structurize-hiccup hiccup)))
