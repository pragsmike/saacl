(ns saacl.t-soap
  (:import (javax.xml.soap SOAPMessage)
           (org.w3c.dom Node Document))
  (:require [saacl.soap :refer :all]
            [saacl.xml :as xml]
            [clojure.test :refer :all]
            [clojure.java.io :as io])
  )

(def response (str "<?xml version='1.0' encoding='UTF-8'?>
<soap:Envelope xmlns:soap='" NS-SOAP "' xmlns='urn:foo'>
  <soap:Header>
    <content-type>text/plain</content-type>
  </soap:Header>
  <soap:Body><one/></soap:Body>
</soap:Envelope>
"))

(def response-no-header (str "<?xml version='1.0' encoding='UTF-8'?>
<soap:Envelope xmlns:soap='" NS-SOAP "' xmlns='urn:foo'>
  <soap:Body><one/></soap:Body>
</soap:Envelope>
"))

(defn is-right-soap? [it]
  (instance? SOAPMessage it)
  )

(deftest test->soap

  (is (is-right-soap? (->soap response)))
  (is (is-right-soap? (->soap (io/input-stream (.getBytes response)))))
  (is (is-right-soap? (->soap (.getBytes response))))


  (let [sm (->soap (.getBytes response))
        hdrs (iterator-seq (.examineAllHeaderElements (.getSOAPHeader sm)))]
    (is (first hdrs))))

(deftest test-get-soap-headers
  (is (= {"content-type" "text/plain"} (get-soap-headers (->soap response)))))

(deftest test-get-soap-headers-when-no-header-element
  (is (= { } (get-soap-headers (->soap response-no-header)))))

(deftest test-get-payload-from-soap
  (let [body (get-payload-from-soap (->soap response) "application/xml")]
    (is (instance? Node body))
    (is (instance? Document body))
    (is (= "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<one/>\n" (xml/->string body)))))

(deftest test-build-soap-xml-text-plain-body
  (let [soap (build-soap-xml {:content-type "text/plain"} "Text!")]
    (is (is-right-soap? soap))
    (is (= "Text!" (.getTextContent (.getSOAPBody soap))))
    (is (=  {"content-type" "text/plain"} (get-soap-headers soap)))))

(deftest test-build-soap-xml-xml-body
      (let [soap (build-soap-xml {:content-type "application/xml"} (xml/->doc "<one/>"))]
        (is (is-right-soap? soap))
        (is (= {"content-type" "application/xml"} (get-soap-headers soap)))
        ))

(deftest test-set-as-payload!-xml
  (let [soap (build-soap {:content-type "application/xml" } "<one/>")]
    (is (instance? Document (get-payload-from-soap soap "application/xml")))))

(deftest test-set-as-payload!-text
  (let [soap (build-soap {:content-type "text/plain" } "Hey!")]
    (= "Hey!" (get-payload-from-soap soap "text/plain"))))
