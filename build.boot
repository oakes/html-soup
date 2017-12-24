(set-env!
  :dependencies '[[org.clojars.oakes/boot-tools-deps "0.1.4" :scope "test"]]
  :repositories (conj (get-env :repositories)
                  ["clojars" {:url "https://clojars.org/repo/"
                              :username (System/getenv "CLOJARS_USER")
                              :password (System/getenv "CLOJARS_PASS")}]))

(require '[boot-tools-deps.core :refer [deps]])

(task-options!
  pom {:project 'html-soup
       :version "1.5.3-SNAPSHOT"
       :description "A library to add HTML tags to Clojure(Script) code"
       :url "https://github.com/oakes/html-soup"
       :license {"Public Domain" "http://unlicense.org/UNLICENSE"}}
  push {:repo "clojars"})

(deftask local []
  (comp (deps) (pom) (jar) (install)))

(deftask deploy []
  (comp (deps) (pom) (jar) (push)))

