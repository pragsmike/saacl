(defproject saacl "0.1.6"
  :description "SOAP with Attachments SAAJ wrapper"
  :url "https://bitbucket.org/pragsmike/saacl"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.github.kyleburton/clj-xpath "1.4.11"]
                 [xerces/xercesImpl "2.11.0"]
                 ]

  :profiles
  {
   :dev     {:dependencies [[org.slf4j/slf4j-log4j12 "1.7.25"]]
             :env          {:dev true  }
             :test-paths   ["test-resources"]
             }
   }
  )
