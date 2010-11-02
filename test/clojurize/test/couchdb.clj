(ns clojurize.test.couchdb
  (:use [clojurize.core] :reload)
  (:use [clojurize.couchdb] :reload)
  (:use [clojure.test])
  (:import [clojurize.couchdb CouchDB]))

(def couch (CouchDB. "http://127.0.0.1:5984/"))
(clojurize widgets couch)

(delete couch "sprockets")
(delete couch "widgets")
(create couch "widgets")

;; need fixtures


(deftest test-database-url
  (are [x y] (= x y)
       (database-url widgets) "http://127.0.0.1:5984/widgets"
       (database-url couch "sprockets") "http://127.0.0.1:5984/sprockets"))

(deftest test-describe-resource-params
  (let [ds (describe-resource couch)
        db (describe-resource widgets)]
    (are [x y] (= x y)
         (:method ds) :get
         (:method db) :get
         (:url ds) "http://127.0.0.1:5984/"
         (:url db) "http://127.0.0.1:5984/widgets")))

(deftest test-list-databases-params
  (let [params (list-databases widgets)]
    (are [x y] (= x y)
         (:method params) :get
         (:url params) "http://127.0.0.1:5984/_all_dbs")))

(deftest test-database-info-params
  (let [db1 (database-info widgets)
        db2 (database-info couch "sprockets")]
    (are [x y] (= x y)
         (:method db1) :get
         (:method db2) :get
         (:url db1) "http://127.0.0.1:5984/widgets"
         (:url db2) "http://127.0.0.1:5984/sprockets")))

(deftest test-create-database-params
  (let [db1 (create-database widgets)
        db2 (create-database couch "sprockets")]
    (are [x y] (= x y)
         (:method db1) :put
         (:method db2) :put
         (:url db1) "http://127.0.0.1:5984/widgets"
         (:url db2) "http://127.0.0.1:5984/sprockets")))

(deftest test-delete-database-params
  (let [db1 (delete-database widgets)
        db2 (delete-database couch "sprockets")]
    (are [x y] (= x y)
         (:method db1) :delete
         (:method db2) :delete
         (:url db1) "http://127.0.0.1:5984/widgets"
         (:url db2) "http://127.0.0.1:5984/sprockets")))

(deftest test-list-documents-params
  (let [db1 (list-documents widgets)
        db2 (list-documents couch "sprockets")]
    (are [x y] (= x y)
         (:method db1) :get
         (:method db2) :get
         (:url db1) "http://127.0.0.1:5984/widgets/_all_docs"
         (:url db2) "http://127.0.0.1:5984/sprockets/_all_docs")))

(deftest test-get-document-params
  (let [params (get-document widgets "doc-id")]
    (are [x y] (= x y)
         (:method params) :get
         (:url params) "http://127.0.0.1:5984/widgets/doc-id")))

(deftest test-create-document-params
  (let [data {:id "doc-id" :name "Widget X"}
        params (create-document widgets data)]
    (are [x y] (= x y)
         (:method params) :put
         (:url params) "http://127.0.0.1:5984/widgets/doc-id"
         (:body params) "{\"id\":\"doc-id\",\"name\":\"Widget X\"}")))

(deftest test-delete-document-params
  (let [data {:_id "doc-id" :_rev "1-23456789"}
        params (delete-document widgets data)]
    (are [x y] (= x y)
         (:method params) :delete
         (:url params) "http://127.0.0.1:5984/widgets/doc-id?rev=1-23456789")))

(deftest test-revisioned
  (let [revisioned {:_id "xyz" :_rev "1-123"}
        not-revisioned {:id "xyz"}
        not-complete {:_id "xyz"}]
    (are [x y] (= x y)
         (revisioned? revisioned) "1-123"
         (revisioned? not-revisioned) nil
         (revisioned? not-complete) nil)))


;;;;;;; common tests

(deftest test-describe-datastore
  (let [response (describe couch)]
    (is (= (:couchdb response) "Welcome"))))

(deftest test-describe-database
  (let [response (describe widgets)]
    (is (= (:db_name response) "widgets"))))

(deftest test-create-database
  (let [response (create couch "sprockets")]
    (is (= response {:ok true}))))

(deftest test-create-tuple
  (let [response (create widgets
                  {:id "widget-x" :name "Widget X"
                   :description "All about Widget X"})]
    (are [x y] (= x y)
         (:ok response) true
         (:id response) "widget-x"
         (string? (:rev response)) true)))

;(deftest test-create-tuples)

(deftest test-search-list-databases
  (let [response (search couch)]
    (is (> (count response) 0))))

; THIS IS BRITTLE, NEED FIXTURES!!!!
(deftest test-search-all-tuples
  (let [response (search widgets)]
    (is (= (:total_rows response) 0))))

(deftest test-search-for-tuple-by-key
  (let [doc (create widgets {:id "doc" :name "Document"})
        response (search widgets "doc")]
    (is (= (:name response) "Document"))))


;(deftest test-search-for-tuple-not-found)
;(deftest test-search-for-tuples-by-keys)
;(deftest test-search-for-tuples-not-found)
;(deftest test-search-for-tuples-by-conditions)
;(deftest test-search-by-conditions-none-found)
;(deftest test-search-map-reduce)

(deftest test-update-tuple
  (let [doc (create widgets {:id "doc2" :name "Doc 2"})
        doc (search widgets "doc2")
        response (update widgets (assoc doc :description "Whatever"))]
    (is (= (:ok response) true))))

(deftest test-update-unrevisioned-tuple
  (let [doc {:id "doc3" :name "Doc 3"}
        response (update widgets doc)]
    (is (= response false))))

;(deftest test-update-tuples)

(deftest test-delete-database
  (let [db (create couch "products")
        response (delete couch "products")]
    (is (= (:ok response) true))))

(deftest test-delete-tuple-by-key
  (let [doc (create widgets {:id "doc3" :name "Doc 3"})
        doc (search widgets "doc3")
        response (delete widgets doc)]
    (is (= (:ok response) true)))) 

;(deftest test-delete-tuples-by-keys)
;(deftest test-delete-tuples-by-conditions)


(deftest test-save-new-tuple
  (let [response (save widgets {:id "doc4" :name "Doc 4"})]
    (is (= (:ok response) true))))

(deftest test-save-changed-tuple
  (let [doc (create widgets {:id "doc5" :name "Doc 5"})
        doc (search widgets "doc5")
        doc (assoc doc :description "Whiz bang widget.")
        response (save widgets doc)]
    (is (= (:ok response) true))))



