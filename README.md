[![Clojars Project](https://img.shields.io/clojars/v/html-soup.svg)](https://clojars.org/html-soup)

## Introduction

A Clojure and ClojureScript library that turns this...

```clojure
(def person {:name "Alice" :age 25})
```

...into this...

```html
<span class='collection list'><span class='delimiter'>(</span><span class='symbol'>def</span> <span class='symbol'>person</span> <span class='collection map'><span class='delimiter'>{</span><span class='keyword'>:name</span> <span class='string'>&quot;Alice&quot;</span> <span class='keyword'>:age</span> <span class='number'>25</span><span class='delimiter'>}</span></span><span class='delimiter'>)</span></span>
```

...or this...

```clojure
[:span {} "" [:span {:class "collection list"} [:span {:class "delimiter"} "("] [:span {:class "symbol"} "def"] " " [:span {:class "symbol"} "person"] " " [:span {:class "collection map"} [:span {:class "delimiter"} "{"] [:span {:class "keyword"} ":name"] " " [:span {:class "string"} "\"Alice\""] " " [:span {:class "keyword"} ":age"] " " [:span {:class "number"} "25"] [:span {:class "delimiter"} "}"]] [:span {:class "delimiter"} ")"]]]
```

Each semantically meaningful token in the Clojure(Script) code is wrapped in `span` tags with a descriptive CSS class. The actual CSS is up to you to define. Example use:

```clojure
(code->html "(+ 1 1)")
; => "<span class='collection list'><span class='delimiter'>(</span><span class='symbol'>+</span> <span class='number'>1</span> <span class='number'>1</span><span class='delimiter'>)</span></span>"

(code->hiccup "(+ 1 1)")
; => [:span {} "" [:span {:class "collection list"} [:span {:class "delimiter"} "("] [:span {:class "symbol"} "+"] " " [:span {:class "number"} "1"] " " [:span {:class "number"} "1"] [:span {:class "delimiter"} ")"]]]
```

## Usage

You can include this library in your project dependencies using the version number in the badge above.

To experiment with this library in a REPL, you can use [the Clojure CLI tool](https://clojure.org/guides/getting_started#_clojure_installer_and_cli_tools). In this directory, run `clj` to start a Clojure REPL, or `clj -A:dev -m cljs.repl.node` to start a ClojureScript REPL. When the REPL is up, enter the main namespace with `(require 'html-soup.core) (in-ns 'html-soup.core)`.

## Licensing

All files that originate from this project are dedicated to the public domain. I would love pull requests, and will assume that they are also dedicated to the public domain.
