(defproject saacl "0.1.5"
  :description "SOAP with Attachments SAAJ wrapper"
  :url "https://bitbucket.org/pragsmike/saacl"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [com.github.kyleburton/clj-xpath "1.4.5"]
                 [xerces/xercesImpl "2.9.1"]
                 ]

  :profiles
  {
   :dev     {:dependencies [[org.slf4j/slf4j-log4j12 "1.7.5"]                            ]
             :env          {:dev true  }
             :test-paths   ["test-resources"]
             }
   }
  )
