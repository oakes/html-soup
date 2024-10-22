[![Clojars Project](https://img.shields.io/clojars/v/html-soup.svg)](https://clojars.org/html-soup)

## Introduction

A Clojure and ClojureScript library that turns this...

```clojure
(def person {:name "Alice" :age 25})
```

...into this...

```html
<span class='collection'><span class='delimiter'>(</span><span class='symbol'>def</span> <span class='symbol'>person</span> <span class='collection'><span class='delimiter'>{</span><span class='keyword'>:name</span> <span class='string'>&quot;Alice&quot;</span> <span class='keyword'>:age</span> <span class='number'>25</span><span class='delimiter'>}</span></span><span class='delimiter'>)</span></span>
```

...or this...

```clojure
[:span {} [:span {:class "collection"} [:span {:class "delimiter"} "("] [:span {:class "symbol"} "def"] " " [:span {:class "symbol"} "person"] " " [:span {:class "collection"} [:span {:class "delimiter"} "{"] [:span {:class "keyword"} ":name"] " " [:span {:class "string"} "\"Alice\""] " " [:span {:class "keyword"} ":age"] " " [:span {:class "number"} "25"] [:span {:class "delimiter"} "}"]] [:span {:class "delimiter"} ")"]]]
```

Each semantically meaningful token in the Clojure(Script) code is wrapped in `span` tags with a descriptive CSS class. The actual CSS is up to you to define.

### [Try the interactive docs!](https://oakes.github.io/html-soup/)

## Licensing

All files that originate from this project are dedicated to the public domain. I would love pull requests, and will assume that they are also dedicated to the public domain.
