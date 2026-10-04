(ns produtor-consumidor
  (:import [java.net ServerSocket Socket]
           [java.io PrintWriter BufferedReader InputStreamReader]))

(defn produzir-dados []
  (repeatedly 100 #(rand-int 111)))

(defn rodar-produtor []
  (println "iniciou (Produtor - Processo 1)")
  (println "# produzir - aguardando conexão no arquivo/porta 12345...")
  
  (with-open [server (ServerSocket. 12345)
              socket (.accept server)
              out (PrintWriter. (.getOutputStream socket) true)]
    (println "# produzir - cliente conectado, gerando dados...")
    (let [dados (produzir-dados)]
      (.println out (pr-str dados))
      (println "# produzir - dados enviados e finalizado")))
  (println "finalizou"))

(defn consumir-dados [dados]
  (let [resultado (reduce + dados)]
    (println "### recebeu ->" resultado)))

(defn rodar-consumidor [host]
  (println "iniciou (Consumidor - Processo 2)")
  (println "### consumir - conectando ao servidor em" host "porta 12345...")
  (with-open [socket (Socket. host 12345)
              in (BufferedReader. (InputStreamReader. (.getInputStream socket)))]
    (println "### consumir - iniciado")
    (let [linha (.readLine in)
          dados (clojure.edn/read-string linha)]
      (consumir-dados dados))
    (println "### consumir - terminado"))
  (println "finalizou"))
(defn -main [& args]
  (let [host (first args)]
    (if (and host (not= host "*command-line-args*"))
      (rodar-consumidor host)  ; Se recebeu o IP/host como argumento, roda o CONSUMIDOR
      (rodar-produtor))))      ; Se rodou sem argumentos, roda o PRODUTOR
(-main)
