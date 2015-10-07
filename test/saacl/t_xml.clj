(ns saacl.t-xml
  (:require [saacl.xml :as xml]
            [clojure.java.io :as io]
            [clojure.test :refer :all]
            [clj-xpath.core :as xp]
            [saacl.soap :as soap]))

(def doc (xp/xml->doc "<abc/>"))
(def doc-node (.getDocumentElement doc))
(def doc-bytes (.getBytes "<abc/>"))

(deftest test-pprint
  (is (= (with-out-str (xml/pprint doc)) "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<abc/>\n"))
  (is (= (with-out-str (xml/pprint doc-node))  "<?xml version=\"1.0\" encoding=\"UTF-8\"?><abc/>"))
  (is (= (with-out-str (xml/pprint doc-bytes)) "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<abc/>\n"))
      )

(deftest test->string
  (is (= (xml/->string doc) "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<abc/>\n"))
  (is (= (xml/->string doc-node)  "<?xml version=\"1.0\" encoding=\"UTF-8\"?><abc/>"))
  (is (= (xml/->string doc-bytes) "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<abc/>\n")))

(deftest test->doc
  (is (= doc (xml/->doc doc)))
  (is (= doc (xml/->doc doc-node)))
  (is (not (= doc (xml/->doc doc-bytes)))))
