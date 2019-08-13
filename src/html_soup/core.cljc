(ns html-soup.core
  (:require [clojure.string :as str]
            [tag-soup.core :as ts]
            [clojure.spec.alpha :as s :refer [fdef]]
            #?(:cljs [goog.string :as gstring :refer [format]])))

(fdef split-lines
  :args (s/cat :str string?)
  :ret (s/coll-of string?))

(defn split-lines
  "Splits the string into lines."
  [s]
  (vec (.split s "\n" -1)))

(fdef escape-html-str
  :args (s/cat :str string?)
  :ret string?)

(defn escape-html-str
  "Escapes an HTML string"
  [s]
  (str/escape s
    {\< "&lt;"
     \> "&gt;"
     \& "&amp;"
     \" "&quot;"
     \' "&apos;"}))

(fdef escape-html-char
  :args (s/cat :str char?)
  :ret (s/or :str string? :char char?))

(defn escape-html-char
  "Escapes an HTML character"
  [s]
  (case s
    \< "&lt;"
    \> "&gt;"
    \& "&amp;"
    \" "&quot;"
    \' "&apos;"
    s))

(fdef tag->html
  :args (s/cat :tag :tag-soup.core/tag)
  :ret string?)

(defn tag->html
  "Returns an HTML string for the given tag description."
  [tag]
  (cond
    (:delimiter? tag) "<span class='delimiter'>"
    (:error? tag) (format
                    "<span class='error' data-message='%s'></span>"
                    (some-> (:message tag) escape-html-str))
    (:begin? tag) (let [value (:value tag)]
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
    (:end? tag) "</span>"))

(fdef tag->hiccup
  :args (s/cat :tag :tag-soup.core/tag)
  :ret (s/coll-of any?))

(defn tag->hiccup
  "Returns a Hiccup-compatible data structure for the given tag description."
  [tag]
  (cond
    (:delimiter? tag) [:span {:class "delimiter"}]
    (:error? tag) [:span {:class "error" :data-message (:message tag)}]
    (:begin? tag) (let [value (:value tag)]
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
    (:end? tag) nil))

(def tags-for-line->html
  (comp
    (partition-by :column)
    (map #(str/join (map tag->html %)))))

(def tags-for-line->hiccup
  (comp
    (partition-by :column)
    (map #(map tag->hiccup %))))

(fdef line->segments
  :args (s/cat :line string? :tags-for-line (s/nilable :tag-soup.core/tags-for-line) :escape? boolean?)
  :ret (s/coll-of string?))

(defn line->segments
  "Splits a line into segments where tags are supposed to appear."
  [line tags-for-line escape?]
  (let [columns (set (map :column tags-for-line))
        escape-html-char (if escape? escape-html-char identity)]
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

(fdef line->html
  :args (s/cat :line string? :tags-for-line (s/nilable :tag-soup.core/tags-for-line))
  :ret string?)

(defn line->html
  "Returns the given line with html added."
  [line tags-for-line]
  (let [html-per-column (into [] tags-for-line->html (sort-by :column tags-for-line))
        segments (line->segments line tags-for-line true)]
    (str/join (interleave segments (concat html-per-column (repeat ""))))))

(fdef line->hiccup
  :args (s/cat :line string? :tags-for-line (s/nilable :tag-soup.core/tags-for-line))
  :ret (s/coll-of any?))

(defn line->hiccup
  "Returns the given line with Hiccup-compatible data structures added."
  [line tags-for-line]
  (let [hiccup-per-column (into [] tags-for-line->hiccup (sort-by :column tags-for-line))
        segments (map list (line->segments line tags-for-line false))]
    (apply concat (interleave segments (concat hiccup-per-column (repeat nil))))))

(fdef parse-lines
  :args (s/cat :parse-fn fn? :lines (s/coll-of string?) :tags :tag-soup.core/all-tags)
  :ret (s/coll-of any?))

(defn parse-lines
  "Returns the lines parsed with the given function."
  [parse-fn lines tags]
  (loop [i 0
         results (transient [])]
    (if-let [line (get lines i)]
      (recur (inc i) (conj! results (parse-fn line (get tags (inc i)))))
      (persistent! results))))

(fdef code->html
  :args (s/cat :code string?)
  :ret string?)

(defn code->html
  "Returns the code in the given string with html added."
  [code]
  (let [lines (split-lines code)
        tags (ts/code->tags code)
        lines (parse-lines line->html lines tags)]
    (str/join \newline lines)))

(fdef structurize-hiccup
  :args (s/alt
          :one-arg (s/cat :flat-hiccup (s/coll-of any?))
          :two-args (s/cat :flat-hiccup (s/coll-of any?) :structured-hiccup (s/coll-of any?)))
  :ret (s/coll-of any?))

(defn structurize-hiccup
  "Takes a flat list of Hiccup-compatible data and adds structure to it."
  ([flat-hiccup]
   (second (structurize-hiccup flat-hiccup [:span {}])))
  ([flat-hiccup structured-hiccup]
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

(fdef code->hiccup
  :args (s/cat :code string?)
  :ret (s/coll-of any?))

(defn code->hiccup
  "Returns the code in the given string with Hiccup-compatible data structures added."
  [code]
  (let [lines (split-lines code)
        tags (ts/code->tags code)
        hiccup (parse-lines line->hiccup lines tags)
        hiccup (apply concat (interpose ["\n"] hiccup))]
    (structurize-hiccup hiccup)))

