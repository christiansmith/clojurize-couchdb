# clojurize-couchdb

clojurize-couchdb is an experimental CouchDB adapter implementing the Clojurize protocol. Read about Clojurize here: 
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

    $ git clone http://github.com/christiansmith/clojurize-couchdb.git
    $ cd clojurize-couchdb
    $ lein deps
    $ lein repl

## License

Copyright (C) 2010 Christian Smith

Distributed under the Eclipse Public License, the same as Clojure.