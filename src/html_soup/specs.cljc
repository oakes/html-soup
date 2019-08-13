(ns html-soup.specs
  (:require [html-soup.core :as c]
            [clojure.spec.alpha :as s :refer [fdef]]))

(fdef c/split-lines
  :args (s/cat :str string?)
  :ret (s/coll-of string?))

(fdef c/escape-html-str
  :args (s/cat :str string?)
  :ret string?)

(fdef c/escape-html-char
  :args (s/cat :str char?)
  :ret (s/or :str string? :char char?))

(fdef c/tag->html
  :args (s/cat :tag :tag-soup.core/tag)
  :ret string?)

(fdef c/tag->hiccup
  :args (s/cat :tag :tag-soup.core/tag)
  :ret (s/nilable (s/coll-of any?)))

(fdef c/line->segments
  :args (s/cat :line string? :tags-for-line (s/nilable :tag-soup.core/tags-for-line) :escape? boolean?)
  :ret (s/coll-of string?))

(fdef c/line->html
  :args (s/cat :line string? :tags-for-line (s/nilable :tag-soup.core/tags-for-line))
  :ret string?)

(fdef c/line->hiccup
  :args (s/cat :line string? :tags-for-line (s/nilable :tag-soup.core/tags-for-line))
  :ret (s/coll-of any?))

(fdef c/parse-lines
  :args (s/cat :parse-fn fn? :lines (s/coll-of string?) :tags :tag-soup.core/all-tags)
  :ret (s/coll-of any?))

(fdef c/code->html
  :args (s/cat :code string?)
  :ret string?)

(fdef c/structurize-hiccup
  :args (s/alt
          :one-arg (s/cat :flat-hiccup (s/coll-of any?))
          :two-args (s/cat :flat-hiccup (s/coll-of any?) :iterator any? :structured-hiccup (s/coll-of any?)))
  :ret (s/coll-of any?))

(fdef c/code->hiccup
  :args (s/cat :code string?)
  :ret (s/coll-of any?))

