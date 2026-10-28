;;servidor chat:                                                                                                                                                                                                                                                                                
(ns servidor-chat
  (:import [java.net ServerSocket Socket SocketTimeoutException]
           [java.io BufferedReader InputStreamReader PrintWriter])
  (:gen-class))

(defn -main []
  (let [port 8082
        servidor (ServerSocket. port)
       
        clientes (atom [])]

    
    (.setSoTimeout servidor 50)
    (println "Servidor de chat iniciado!")

    (try
      (loop []
       
          (let [novo-socket (.accept servidor)]
            (.setSoTimeout novo-socket 10)
            (let [cliente {:socket novo-socket
                           :in (BufferedReader. (InputStreamReader. (.getInputStream novo-socket)))
                           :out (PrintWriter. (.getOutputStream novo-socket) true)}]
              (swap! clientes conj cliente)
              (println "Novo cliente conectado!")))
          (catch SocketTimeoutException _
            ))
;; 2. Processa as mensagens e desconexões dos clientes conectados
        (doseq [cliente @clientes]
          (try
            (when-let [mensagem (.readLine (:in cliente))]
              (println (str "Mensagem: " mensagem))

              
              (doseq [outro @clientes]
                (when-not (= outro cliente)
                  (.println (:out outro) mensagem))))

            (catch SocketTimeoutException 
              
              nil)
            (catch Exception 
              
              (println "Cliente desconectado!")
              (try (.close (:socket cliente)) (catch Exception _ nil))
              (swap! clientes (fn [lista] (remove #(= % cliente) lista))))))

    
        (Thread/sleep 50)
        (recur))

      (finally
        (.close servidor)))))