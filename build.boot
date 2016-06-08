(set-env!
  :source-paths #{"src"}
  :dependencies '[[adzerk/boot-cljs "1.7.228-1" :scope "test"]
                  ; project deps
                  [org.clojure/clojure "1.8.0"]
                  [org.clojure/clojurescript "1.8.51"]
                  [prismatic/schema "0.4.3"]
                  [tag-soup "1.3.2"]])

(require
  '[adzerk.boot-cljs :refer [cljs]])

(deftask run-repl []
  (repl :init-ns 'html-soup.core))

(deftask build []
  (set-env! :source-paths #{"src"})
  (comp (cljs :optimizations :advanced)))
