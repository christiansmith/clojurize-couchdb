(ns clojurize.couchdb
  (:require [clj-http.client :as client])
  (:use [clojure.contrib.json :only (read-json json-str)])
  (:use [clojurize.core]))


(def http-defaults {:accept :json})

(defn request
  [params]
  (client/request
   (merge http-defaults params)))

(defn enclojurize
  "Translate the CouchDB response into
   Clojure data."
  [response]
  (read-json (:body response)))

(def declojurize json-str)

;; URL building helpers

(defn database-url
  [adapter & [name]]
  (str (:url adapter) (or name (:database adapter))))

(defn all-docs-url
  [adapter & [name]]
  (str (database-url adapter name) "/_all_docs"))

(defn all-dbs-url [adapter]
  (str (:url adapter) "_all_dbs"))

(defn document-url
  [adapter id & [name]]
  (str (database-url adapter name) "/" id))

(defn delete-doc-url
  [adapter {:keys [_id _rev]} & [name]]
  (str (document-url adapter _id name) "?rev=" _rev))


;; Request param building helpers

(defn build-params
  [method url & [body]]
  {:method method
   :url url
   :body body})

(defn describe-resource
  [adapter]
  (build-params :get (database-url adapter)))

(defn list-databases
  [adapter]
  (build-params :get (all-dbs-url adapter)))

(defn database-info
  [adapter & [name]]
  (build-params :get (database-url adapter name)))

(defn create-database
  [adapter & [name]]
  (build-params :put (database-url adapter name)))

(defn delete-database
  [adapter & [name]]
  (build-params :delete (database-url adapter name)))

(defn list-documents
  [adapter & [name]]
  (build-params :get (all-docs-url adapter name)))

(defn get-document
  [adapter key & [name]]
  (build-params :get (document-url adapter key name)))

(defn create-document
  [adapter data]
  (build-params
   :put
   (document-url adapter (:id data))
   (declojurize data)))

; edit this to check for _id and _rev as preconditions
(defn update-document
  [adapter data]
  (build-params
   :put
   (document-url adapter (:_id data))
   (declojurize data)))

(defn delete-document
  [adapter data]
  (build-params :delete
                (delete-doc-url adapter data)))

(defn revisioned?
  [{:keys [_id _rev]}]
  (and _id _rev))

(defrecord CouchDB [url]
  Clojurize

  (describe
   [adapter]
   (enclojurize (request (describe-resource adapter))))

  (create
   [adapter params]
   (enclojurize
    (request
     ((if (:database adapter)
        create-document
        create-database)
      adapter
      params))))

  (search
   [adapter]
   (enclojurize
    (request
     ((if (:database adapter)
        list-documents
        list-databases)
      adapter))))

  (search
   [adapter params]
   (enclojurize
    (request
     (get-document adapter params))))

  (update
   [adapter tuples]
   (if (revisioned? tuples)
     (enclojurize (request (update-document adapter tuples)))
     false))

  (delete
   [adapter params]
   (enclojurize
    (request
     ((if (:database adapter)
        delete-document
        delete-database)
      adapter
      params))))

  (save
   [adapter tuples]
   (cond
    (revisioned? tuples) (update adapter tuples)
    (:id tuples) (create adapter tuples)
    :else false))
  
  (prompt [adapter params]
          (enclojurize (request params))))


(defmacro clojurize
  "Convenience macro for wrapping up a database.
   Example:
     (clojurize widgets (CouchDB. \"http://127.0.0.1:5984/\")
     => clojurize.couchdb.CouchDB{:url ..., :database \"widgets\"}"
  [name adapter]
  (let [n (str name)]
    `(def ~name (assoc ~adapter :database ~n))))

