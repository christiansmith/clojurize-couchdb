# clojurize-couchdb

clojurize-couchdb is an experimental [CouchDB](http://couchdb.apache.org/) adapter implementing the [Clojurize protocol](http://github.com/christiansmith/clojurize-protocol). Read about Clojurize at [anvil.io](http://anvil.io/):
  
http://anvil.io/2010/10/25/clojurize-the-data-not-the-database.html

## Usage

    ;; configure
    (use 'clojurize.core 'clojurize.couchdb)
    (import '[clojurize.couchdb CouchDB])
    
    (def datastore (CouchDB. "http://127.0.0.1:5984/"))

    ;; datastore instance info
    (describe datastore)

    ;; list databases
    (search datastore)
    
    ;; create a database
    (create datastore "widgets")

    ;; create an instance coupled to the widgets database
    (clojurize widgets datastore)

    ;; widgets database info
    (describe widgets)

    ;; create a widget tuple 
    (create widgets 
      {:id "widget-x" :name "Widget X"
       :description "All about Widget X"})

    ;; lookup the tuple by it's key
    (search widgets "widget-x")

    ;; update the tuple
    (let [w (search widgets "widget-x")]
      (update widgets (merge w { :size "mini" }))

    ;; and so on

    
## Installation

Add `\[clojurize-couchdb "1.0.0-SNAPSHOT"\]` to `:dependencies` in your
project.clj file and run `lein deps`, or:

    $ git clone http://github.com/christiansmith/clojurize-couchdb.git
    $ cd clojurize-couchdb
    $ lein deps
    $ lein repl

## License

Copyright (C) 2010 Christian Smith

Distributed under the Eclipse Public License, the same as Clojure.
