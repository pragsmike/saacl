(ns saacl.soap
  (:import (javax.xml.transform.stream StreamSource)
           (javax.xml.soap SOAPMessage MessageFactory MimeHeaders SOAPConstants)
           (javax.xml.transform.dom DOMSource)
           (org.w3c.dom DOMException Node Document)
           (java.io InputStream)
           (javax.xml.transform Source))
  (:require [clojure.java.io :as io]
            [clj-xpath.core :as xp]
            [saacl.xml :as xml]))

(def NS-SOAP "http://schemas.xmlsoap.org/soap/envelope/")
(def NS-ADDRESSING "http://schemas.xmlsoap.org/ws/2004/08/addressing")

(defmethod xp/xml->doc javax.xml.transform.dom.DOMSource [thing & [opts]] (.getNode thing))
(defmethod xp/xml->doc javax.xml.soap.SOAPMessage [thing & [opts]] (.getContent (.getSOAPPart thing)))
(defmethod xp/xml->doc javax.xml.soap.SOAPPart [thing & [opts]] (.getContent thing))

(defprotocol XmlSoap
  (->soap [in]))

(defn mime-headers []
  (let [mh (MimeHeaders.)]
    (.addHeader mh "content-type" "application/soap+xml")
    mh))

(extend-protocol XmlSoap
  ; yuck http://dev.clojure.org/jira/browse/CLJ-1381
  ; intellij thinks syntax error but it's ok
  (Class/forName "[B")
    (->soap [in] (->soap (io/input-stream in)))

  SOAPMessage
    (->soap [in] in)

  String
    (->soap [in] (->soap (io/input-stream (.getBytes in))))

  InputStream
    (->soap [in] (-> (MessageFactory/newInstance SOAPConstants/SOAP_1_2_PROTOCOL)
                     (.createMessage (mime-headers) in)
                     ))

  Source
    (->soap [in] (-> (MessageFactory/newInstance SOAPConstants/SOAP_1_2_PROTOCOL)
                     (.createMessage)
                     (.getSOAPPart)
                     (.setContent in)))

  Object
    (->soap [in] (->soap (xml/->source in)))
  )


;--------------------------------------------
; Parse SOAP documents

(defn soap? [it]
  (or (instance? SOAPMessage it)
      (if-let [xml (xml/->doc it)]
        (re-find #"Envelope" (.getNodeName (.getDocumentElement xml))))
      )
  )

(defn get-soap-headers
  "Given a SOAP XML envelope, returns a map of the SOAP header names and their text as strings.
  The keys in the map will be the string names of the child elements of the SOAP Header."
  [soap]

  (into {} (map #(vector (.toLowerCase (.getLocalName %))
                         (.getTextContent %))
                (iterator-seq (.examineAllHeaderElements (.getSOAPHeader soap)))
                ))
  )



(defn get-payload-from-soap
  "Given a SOAPMessage that contains a ROAP-encoded request or response,
  and a a MIME content-type as a string, extract the payload object that was
  encoded in the SOAP body.  This will be one of
      application/xml  the root of the payload XML as a w3c Element (not the SOAP Body itself)
      String
  "
  [soap content-type]
  (if (re-find #"application/xml" content-type)
    (.extractContentAsDocument (.getSOAPBody soap))
    (.getTextContent (.getSOAPBody soap))
    )
  )

;-----------------------------------------------
; Build SOAP documents

(defprotocol SOAPBodyContent
  (set-as-body! [content soap])
  )

(extend-protocol SOAPBodyContent
  Document
    (set-as-body! [content soap] (.addDocument (.getSOAPBody soap) content))
  Node
    (set-as-body! [content soap] (set-as-body! (xml/->doc content) soap))
  String
    (set-as-body! [content soap] (.appendChild (.getSOAPBody soap)
                                               (.createTextNode (.getOwnerDocument (.getSOAPBody soap))
                                                                content)))
  nil
    (set-as-body! [content soap] )
  )

(defn soap-name [soap s]
  (-> soap
      (.getSOAPPart)
      (.getEnvelope)
      (.createName s "a", "urn:a"))
  )
(defn build-soap-xml
  "Returns a SOAPMessage.  headers is a map of string header names and values.
  body is anything that does set-as-body!.  Note this is NOT the soap:Body node itself,
  but will be used to construct a child of the soap:Body node."
  [headers body]
  (let [soap (-> (MessageFactory/newInstance SOAPConstants/SOAP_1_2_PROTOCOL)
                 (.createMessage)
                 )]
    (dorun (for [[k v] headers]
             (-> soap
                 (.getSOAPHeader)
                 (.addHeaderElement (soap-name soap (name k)))
                 (.setTextContent (str v)))
             ))
    (set-as-body! body soap)
    soap
    )
  )
