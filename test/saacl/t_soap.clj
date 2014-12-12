(ns saacl.t-soap
  (:import (javax.xml.soap SOAPMessage))
  (:require [saacl.soap :refer :all]
            [saacl.xml :as xml]
            [midje.sweet :refer :all]
            [clojure.java.io :as io])
  )

(def response "<?xml version='1.0' encoding='UTF-8'?>
<soap:Envelope xmlns:soap='http://www.w3.org/2003/05/soap-envelope' xmlns='urn:foo'>
  <soap:Header>
    <content-type>text/plain</content-type>
  </soap:Header>
  <soap:Body><one/></soap:Body>
</soap:Envelope>
")

(defn is-right-soap? [it]
  (instance? SOAPMessage it)
  )

(fact "->soap"

      (->soap response) => is-right-soap?
      (->soap (io/input-stream (.getBytes response))) => is-right-soap?
      (->soap (.getBytes response)) => is-right-soap?


      (let [sm (->soap (.getBytes response))
            hdrs (iterator-seq (.examineAllHeaderElements (.getSOAPHeader sm)))]
        (first hdrs) => truthy
        )
      )

(fact "get-soap-headers"
      (get-soap-headers (->soap response)) => {"content-type" "text/plain"}
      )

(fact "build-soap-xml text/plain body"
      (let [soap (build-soap-xml {:content-type "text/plain"} "Text!")]
        soap => is-right-soap?
        (.getTextContent (.getSOAPBody soap)) => "Text!"
        (get-soap-headers soap) => {"content-type" "text/plain"}
        ))

(fact "build-soap-xml xml body"
      (let [soap (build-soap-xml {:content-type "application/xml"} (xml/->doc "<one/>"))]
        soap => is-right-soap?
        (get-soap-headers soap) => {"content-type" "application/xml"}
        ))
