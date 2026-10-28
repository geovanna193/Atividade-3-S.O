;;servidor echo:                                                                                                                                                                                                                                                                        
(ns servidor-echo
  (:import [java.net ServerSocket]
           [java.io BufferedReader InputStreamReader PrintWriter])
  (:gen-class))

(defn -main []
  (let [port 8081]
    
    (with-open [servidor (ServerSocket. port)]
      (println "Servidor Echo aguardando conexao...")

      
      (with-open [cliente (.accept servidor)
                  in (BufferedReader. (InputStreamReader. (.getInputStream cliente)))
                  out (PrintWriter. (.getOutputStream cliente) true)]
        (println "Cliente conectado!")

        
        (loop []
          (let [mensagem (.readLine in)]
            
            (when mensagem
              (println (str "Recebido: " mensagem))

              
              (.println out mensagem)

             
              (recur))))))))