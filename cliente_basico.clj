;;cliente_básico.clj                                                                                                                                                                                                                                                                       
(ns cliente-socket
  (:import [java.net Socket]
           [java.io BufferedReader InputStreamReader PrintWriter])
  (:gen-class))

(defn -main []
  (let [host "127.0.0.1"
        port 8080]

    (with-open [socket (Socket. host port)
                out (PrintWriter. (.getOutputStream socket) true)
                in (BufferedReader. (InputStreamReader. (.getInputStream socket)))]

      (.println out "Ola, servidor!")


      (let [resposta (.readLine in)]
        (println (str "Resposta do servidor: " (or resposta "")))))))