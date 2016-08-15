(set-env!
  :source-paths #{"src"}
  :resource-paths #{"src"}
  :dependencies '[[org.clojure/clojure "1.9.0-alpha10"]
                  [org.clojure/clojurescript "1.9.211"]
                  [tag-soup "1.3.4-SNAPSHOT"]]
  :repositories (conj (get-env :repositories)
                  ["clojars" {:url "https://clojars.org/repo/"
                              :username (System/getenv "CLOJARS_USER")
                              :password (System/getenv "CLOJARS_PASS")}]))

(task-options!
  pom {:project 'html-soup
       :version "1.2.4-SNAPSHOT"
       :description "A library to add HTML tags to Clojure(Script) code"
       :url "https://github.com/oakes/html-soup"
       :license {"Public Domain" "http://unlicense.org/UNLICENSE"}}
  push {:repo "clojars"})

(deftask run-repl []
  (repl :init-ns 'html-soup.core))

(deftask try []
  (comp (pom) (jar) (install)))

(deftask deploy []
  (comp (pom) (jar) (push)))

