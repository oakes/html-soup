(defproject html-soup "1.1.2-SNAPSHOT"
  :description "A library to add HTML tags to Clojure(Script) code"
  :url "https://github.com/oakes/html-soup"
  :license {:name "Public Domain"
            :url "http://unlicense.org/UNLICENSE"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.7.228"]
                 [prismatic/schema "0.4.3"]
                 [tag-soup "1.2.2"]]
  :profiles {:uberjar {:prep-tasks ["compile" ["cljsbuild" "once"]]}}
  :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]
  :plugins [[lein-cljsbuild "1.1.2"]]
  :cljsbuild {:builds {:main {:source-paths ["src"]
                              :compiler {:output-to "resources/public/html-soup.js"
                                         :optimizations :advanced
                                         :pretty-print false}
                              :jar true}}}
  :main html-soup.core)
