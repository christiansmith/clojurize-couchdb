(ns clojurize.core)

(defprotocol Clojurize
 
  (describe
   [adapter]
   [adapter params]
   "The describe function returns a map aggregating all derivable
    adapter info.")

  (create
   [adapter params]
   "<database|entity|tuple(s)>")

  (search
   [adapter]
   [adapter params]
   "<conditions|mapper/reducer-fns>")
  
  (update
   [adapter tuples]
   "<tuple(s)>")

  (delete
   [adapter params]
   "<database|entity|tuple(s)>")
  
  (modify
   [adapter params]
   "<entity|?>")

  (exists?
   [adapter params]
   "Checks if an object exists and returns a truthy response or false")
  
  (touch
   [adapter params]
   "Equivalent of find-or-create.")
  
  (save
   [adapter tuples]
   "<tuple(s)>")

  (prompt
   [adapter expression]
   "Send a native expression to the data source, e.g. SQL, JSON, etc."))

