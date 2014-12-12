(ns saacl.t-xml
  (:require [saacl.xml :as xml]
            [clojure.java.io :as io]
            [midje.sweet :refer :all]
            [clj-xpath.core :as xp]
            [saacl.soap :as soap]))

(def doc (xp/xml->doc "<abc/>"))
(def doc-node (.getDocumentElement doc))
(def doc-bytes (.getBytes "<abc/>"))

(fact "pprint"
      (with-out-str (xml/pprint doc)) => "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<abc/>\n"
      (with-out-str (xml/pprint doc-node)) => "<?xml version=\"1.0\" encoding=\"UTF-8\"?><abc/>"
      (with-out-str (xml/pprint doc-bytes)) => "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<abc/>\n"
      )

(fact "->string"
      (xml/->string doc) => "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<abc/>\n"
      (xml/->string doc-node) => "<?xml version=\"1.0\" encoding=\"UTF-8\"?><abc/>"
      (xml/->string doc-bytes) => "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<abc/>\n"
      )

(fact "->doc"
      (xml/->doc doc) => doc
      (xml/->doc doc-node) => doc
      (xml/->doc doc-bytes) =not=> doc
      )

