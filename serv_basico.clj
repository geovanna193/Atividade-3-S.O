;;servidor básico:                                                                                                                                                                                                                                                                          
(ns servidor-socket
  (:import [java.net ServerSocket]
           [java.io BufferedReader InputStreamReader PrintWriter])
  (:gen-class))

(defn -main []
  (let [port 8080]
   
    (with-open [servidor (ServerSocket. port)]
      (println "Servidor aguardando conexao...")

      (with-open [cliente (.accept servidor)
                  in (BufferedReader. (InputStreamReader. (.getInputStream cliente)))
                  out (PrintWriter. (.getOutputStream cliente) true)]
        (println "Cliente conectado!")

        
        (let [mensagem (.readLine in)]
          (println (str "Mensagem recebida: " (or mensagem "")))

        
          (.println out "Mensagem recebida pelo servidor!"))))))