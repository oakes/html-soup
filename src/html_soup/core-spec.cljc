(ns html-soup.core-spec
  (:require [html-soup.core :as c]
            [clojure.spec :as s :refer [fdef]]))

(fdef c/split-lines
  :args (s/cat :str string?)
  :ret string?)

(fdef c/escape-html-string
  :args (s/cat :str string?)
  :ret string?)

(fdef c/escape-html-char
  :args (s/cat :str char?)
  :ret string?)

(fdef c/tag->html
  :args (s/cat :tag ::ts/tag)
  :ret string?)

(fdef c/tag->hiccup
  :args (s/cat :tag ::ts/tag)
  :ret (s/coll-of any?))

(fdef c/line->segments
  :args (s/cat :line string? :tags-for-line (s/nilable ::ts/tags-for-line) :escape? boolean?)
  :ret (s/coll-of string?))

(fdef c/line->html
  :args (s/cat :line string? :tags-for-line (s/nilable ::ts/tags-for-line))
  :ret string?)

(fdef c/line->hiccup
  :args (s/cat :line string? :tags-for-line (s/nilable ::ts/tags-for-line))
  :ret (s/coll-of any?))

(fdef c/parse-lines
  :args (s/cat :parse-fn fn? :lines (s/coll-of string?) :tags ::ts/all-tags)
  :ret (s/coll-of any?))

(fdef c/code->html
  :args (s/cat :code string?)
  :ret string?)

(fdef c/structurize-hiccup
  :args (s/alt
          :one-arg (s/cat :flat-hiccup (s/coll-of any?))
          :tow-args (s/cat :flat-hiccup (s/coll-of any?) :structured-hiccup (s/coll-of any?)))
  :ret (s/coll-of any?))

(fdef c/code->hiccup
  :args (s/cat :code string?)
  :ret (s/coll-of any?))

