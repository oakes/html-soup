(defproject html-soup "1.6.1-SNAPSHOT"
  :description "A library to add HTML tags to Clojure(Script) code"
  :url "https://github.com/oakes/html-soup"
  :license {:name "Public Domain"
            :url "http://unlicense.org/UNLICENSE"}
  :plugins [[lein-tools-deps "0.4.3"]]
  :middleware [lein-tools-deps.plugin/resolve-dependencies-with-deps-edn]
  :lein-tools-deps/config {:config-files [:install :user :project]}
  :profiles {:dev {:main html-soup.core}})
