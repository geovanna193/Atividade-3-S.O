;;cliente echo:                                                                                                                                                                                                                                                                             
(ns cliente-interativo
  (:import [java.net Socket]
           [java.io BufferedReader InputStreamReader PrintWriter])
  (:gen-class))

(defn -main []
  (let [host "127.0.0.1"
        port 8081]
   
    (with-open [socket (Socket. host port)
                out (PrintWriter. (.getOutputStream socket) true)
                in (BufferedReader. (InputStreamReader. (.getInputStream socket)))]

      (loop []
   
        (print "Digite uma mensagem (sair para encerrar): ")
        (flush)

        (let [mensagem (read-line)]
         
          (when (and mensagem (not= mensagem "sair"))
            
            (.println out mensagem)

  
            (let [resposta (.readLine in)]
              (println (str "Resposta do servidor: " (or resposta ""))))

            
            (recur)))))))