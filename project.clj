(defproject html-soup "1.6.2-SNAPSHOT"
  :description "A library to add HTML tags to Clojure(Script) code"
  :url "https://github.com/oakes/html-soup"
  :license {:name "Public Domain"
            :url "http://unlicense.org/UNLICENSE"}
  :repositories [["clojars" {:url "https://clojars.org/repo"
                             :sign-releases false}]]
  :profiles {:dev {:main html-soup.core}})
