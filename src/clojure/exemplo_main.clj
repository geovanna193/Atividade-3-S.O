```clojure
(ns exemplo-main)

(defn produzir-dados []
  (repeatedly 100 #(rand-int 111)))

(defn consumir-dados [dados]
  (let [resultado (reduce + dados)]
    (println "### recebeu ->" resultado)))

(defn -main []
  (println "iniciou")
  (let [thread-produtor (future
                          (println "# produzir - iniciado")
                          (let [dados (produzir-dados)]
                            (println "# produzir - terminado")
                            dados))
        thread-consumidor (future
                            (println "### consumir - iniciado")
                            (consumir-dados @thread-produtor)
                            (println "### consumir - terminado"))]
    @thread-consumidor)
  (println "finalizou"))

(-main) ;chamada