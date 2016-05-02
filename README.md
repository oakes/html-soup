## Introduction

A Clojure and ClojureScript library that turns this...

```clojure
(def person {:name "Alice" :age 25})
```

...into this...

```html
<span class='collection list'><span class='delimiter'>(</span><span class='symbol'>def</span> <span class='symbol'>person</span> <span class='collection map'><span class='delimiter'>{</span><span class='keyword'>:name</span> <span class='string'>&quot;Alice&quot;</span> <span class='keyword'>:age</span> <span class='number'>25</span><span class='delimiter'>}</span></span><span class='delimiter'>)</span></span>
```

Each semantically meaningful token in the Clojure(Script) code is wrapped in `span` tags with a descriptive CSS class. The actual CSS is up to you to define. Example use:

```clojure
(code->html "(+ 1 1)")
; => "<span class='collection list'><span class='delimiter'>(</span><span class='symbol'>+</span> <span class='number'>1</span> <span class='number'>1</span><span class='delimiter'>)</span></span>"
```

## Licensing

All files that originate from this project are dedicated to the public domain. I would love pull requests, and will assume that they are also dedicated to the public domain.
