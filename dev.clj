(require
  '[figwheel.main :as figwheel]
  '[dynadoc.core :as dynadoc])

(dynadoc/start {:port 5000
                :exclusions '#{html-soup.core/escape-html-str
                               html-soup.core/node->html
                               html-soup.core/node->hiccup}})
(figwheel/-main "--build" "dev")

