```clojure
(ns exemplo.rede-produtor
  (:import [java.net ServerSocket InetAddress]
           [java.io PrintWriter]))

(defn produzir-dados []
  (repeatedly 100 #(rand-int 111)))

(defn -main []
  (println "iniciou (Computador A - Produtor)")
  
  (let [porta 12345
        bind-ip (InetAddress/getByName "0.0.0.0")] ; Aceita conexões externas
    (println "# produzir - escutando rede na porta" porta "...")
    (with-open [server (ServerSocket. porta 50 bind-ip)
                socket (.accept server)
                out (PrintWriter. (.getOutputStream socket) true)]
      (println "# produzir - computador remoto conectado!")
      (let [dados (produzir-dados)]
        (.println out (pr-str dados))
        (println "# produzir - dados enviados via rede com sucesso!"))))
  (println "finalizou"))


  (ns exemplo.rede-consumidor
  (:import [java.net Socket]
           [java.io BufferedReader InputStreamReader]))
(defn consumir-dados [dados]
  (let [resultado (reduce + dados)]
    (println "### recebeu ->" resultado)))
(defn -main [ip-servidor]
  (let [host (or ip-servidor "192.168.1.100") ; Insira o IP do Computador A aqui
        porta 12345]
    (println "iniciou (Computador B - Consumidor)")
    (println "### conectando ao IP:" host)
    (with-open [socket (Socket. host porta)
                in (BufferedReader. (InputStreamReader. (.getInputStream socket)))]
      (println "### consumir - conexão estabelecida")
      (let [linha (.readLine in)
            dados (clojure.edn/read-string linha)]
        (consumir-dados dados)
        (println "### consumir - terminado"))) 
    (println "finalizou")))
(-main)