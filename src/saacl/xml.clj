(ns saacl.xml
  (:import (javax.xml.transform.stream StreamResult StreamSource)
           (javax.xml.transform TransformerFactory Source)
           (org.apache.xml.serialize OutputFormat XMLSerializer)
           (javax.xml.transform.dom DOMSource DOMResult)
           (java.io InputStream ByteArrayInputStream)
           (org.w3c.dom Document Node)
           (javax.xml.soap SOAPMessage SOAPPart)
           (java.net URL))
  (:require [clj-xpath.core :as xp]
            [clojure.java.io :as io]))

(defprotocol PrettyPrint
  (pprint [it])
  (->string [it])
  )

(defprotocol XmlConvert
  (->source [it])
  (->doc [it])
  )

(extend-protocol XmlConvert
  Source
    (->source [it] it)
    (->doc [it] (->doc (let [result (DOMResult.)]
                         (-> (TransformerFactory/newInstance)
                             (.newTransformer)
                             (.transform it result)
                             )
                         result)))
  DOMResult
    (->doc [it] (->doc (.getNode it)))
  DOMSource
    (->doc [it] (->doc (.getNode it)))

  Document
    (->source [it] (DOMSource. it))
    (->doc [it] it)
  Node
    (->doc [it] (.getOwnerDocument it))                     ; TODO only ok if Node is doc's root, FIX THIS!
  String
    (->doc [it] (binding [xp/*namespace-aware* true] (xp/xml->doc it)))
    (->source [it] (->source (ByteArrayInputStream. (.getBytes it))))
  InputStream
    (->source [it] (StreamSource. it))
    (->doc [it] (binding [xp/*namespace-aware* true] (xp/xml->doc it)))
  URL
    (->source [it] (->source (io/input-stream it)))
    (->doc [it] (->doc (io/input-stream it)))
  SOAPMessage
    (->source [it] (->source (.getSOAPPart it)))
    (->doc [it] (->doc (.getSOAPPart it)))
  SOAPPart
    (->source [it] (->source (.getContent it)))
    (->doc [it] (->doc (.getContent it)))
  Object
    (->doc [it] (binding [xp/*namespace-aware* true] (xp/xml->doc it)))
  nil
    (->doc [it] nil)
  )

(extend-protocol PrettyPrint
  Document
    (pprint [doc] (let [fmt (OutputFormat. doc)]
                    (.setLineWidth fmt 65)
                    (. fmt setIndenting true)
                    (. fmt setIndent 2)
                    (. (XMLSerializer. *out* fmt) serialize doc)
                    ))
    (->string [it] (with-out-str (pprint it)))
  Node
    (pprint [it] (-> (TransformerFactory/newInstance)
                     (.newTransformer)
                     (.transform (DOMSource. it) (StreamResult. *out*))))
    (->string [it] (with-out-str (pprint it)))
  DOMSource
    (pprint [it] (pprint (.getNode it)))
    (->string [it] (->string (.getNode it)))

  InputStream
    (pprint [it] (println (slurp it)))
    (->string [it] (slurp it))

  String
    (pprint [it] (println it))
    (->string [it] it)

  SOAPMessage
    (pprint [it] (pprint (->doc it)))
    ; to string literally, don't prettify, as that corrupts XML signature
    (->string [it]   (let [bos (java.io.ByteArrayOutputStream.)]
                       (.writeTo it bos)
                       (.toString bos)))

  Object
    (pprint [it] (pprint (->doc it)))
    (->string [it] (->string (->doc it)))
  nil
    (pprint [it] (println ""))
    (->string [it] nil)

  )


(defn xml?
  "Returns truthy if given object is a w3c node, or something that parses as XML."
  [it]
  (or (instance? org.w3c.dom.Node it)
      (->doc it))
  )

(defn element? [node] (instance? org.w3c.dom.Element node))
