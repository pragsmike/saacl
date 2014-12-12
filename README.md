# saacl SOAP with Attachments (SAAJ) wrapper for Clojure

Provides pretty-print and some idiomatic wrappers for Java's SOAP (SAAJ) package.


## Usage

```
(require 'saacl.xml)

(def doc (xml/->doc "<top><middle/></top>")

(xml/pprint doc)
```

## License

Copyright © 2014 Mike Gallaher

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
