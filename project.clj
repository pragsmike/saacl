(defproject saacl "0.1.4"
  :description "SOAP with Attachments SAAJ wrapper"
  :url "https://bitbucket.org/pragsmike/saacl"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.github.kyleburton/clj-xpath "1.4.4"]
                 [xerces/xercesImpl "2.9.1"]
                 ]

  :profiles
  {
   :dev     {:dependencies [[midje "1.6.3"]
                            [robert/hooke "1.3.0"]
                            [org.slf4j/slf4j-log4j12 "1.7.5"]
                            ]
             :env          {:dev true  }
             :test-paths   ["test-resources"]
             :plugins      [[lein-midje "3.1.3"]
                            [cider/cider-nrepl "0.7.0"]
                            ]
             :repl-options {:init (use 'midje.repl)}
             }
   }
  )
