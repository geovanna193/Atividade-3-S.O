;;cliente com chat:                                                                                                   
(ns cliente-chat
  (:import [java.net Socket]
           [java.io BufferedReader InputStreamReader PrintWriter])
  (:gen-class))

(defn -main []
  (let [host "127.0.0.1"
        port 8082]
    (with-open [socket (Socket. host port)
                out (PrintWriter. (.getOutputStream socket) true)
                in (BufferedReader. (InputStreamReader. (.getInputStream socket)))]

      
      (.setSoTimeout socket 100)

      (println "Conectado ao chat!")
      (println "Digite suas mensagens (sair para encerrar):")

      (loop []
       
        (let [mensagem (read-line)]
          (when (and mensagem (not= mensagem "sair"))
            
            (.println out mensagem)
            (try
              (when-let [resposta (.readLine in)]
                (println (str "Mensagem recebida: " resposta)))
              (catch java.net.SocketTimeoutException _
            
            
            (recur)))))))