# Relatório sobre implementação de comunicação entre tarefas em FIXME

## Introdução

Este relato faz parte do processo avaliativo da disciplina de sistemas operacionas no curso superior em análise e desenvolvimento de sistemas, ofertado na Diretoria acadêmica de gestão e tecnologia da informação no campus natal-central do instituto federal de educação, ciência e tecnologia do rio grande do norte.

Tem como objetivo principal relatar as implementações de comunicação entre tarefas na linguagem FIXME.

O grupo de trabalho foi formado por FIXME.

## Comunicação entre tarefas em FIXME

### Informações gerais

FIXME
> qual o objetivo de comunicação entre tarefas? 

FIXME
> explicar porque usar docker nesse trabalho.
> qual a configuração do docker?

### Comunicação entre tarefas com linhas de execução no mesmo processo

##Exemplo:
(ns exemplo.mesmo-processo)
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
  (println "finalizou"))

FIXME
> explicar como foi executado
> mostrar as saídas do terminal
> mostrar as saídas do terminal

FIXME
> se houve problema na execução, enumerar os problemas e suas respectivas soluções

### Comunicação entre tarefas em processos diferentes no mesmo computador

FIXME
> texto explicando o código
> (ns exemplo.processo-produtor
  (:import [java.net ServerSocket]
           [java.io PrintWriter]))

(defn produzir-dados []
  (repeatedly 100 #(rand-int 111)))

(defn -main []
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

FIXME
> explicar como foi executado
> mostrar as saídas do terminal
> mostrar as saídas do terminal

FIXME
> se houve problema na execução, enumerar os problemas e suas respectivas soluções

### Comunicação entre tarefas em processos diferentes em computadores diferentes

Exemplo:
> texto explicando o código
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

FIXME
> explicar como foi executado
> mostrar as saídas do terminal
> mostrar as saídas do terminal

FIXME
> se houve problema na execução, enumerar os problemas e suas respectivas soluções

## Considerações finais

FIXME
> conseguiu implementar tudo e executar?
> qual foi o aprendizado nesse trabalho?
> alguma recomendação para próximos alunos?
