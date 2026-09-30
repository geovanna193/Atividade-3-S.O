# Relatório sobre implementação de comunicação entre tarefas em Clojure.

## Introdução

Este relato faz parte do processo avaliativo da disciplina de sistemas operacionas no curso superior em análise e desenvolvimento de sistemas, ofertado na Diretoria acadêmica de gestão e tecnologia da informação no campus natal-central do instituto federal de educação, ciência e tecnologia do rio grande do norte.

Tem como objetivo principal relatar as implementações de comunicação entre tarefas na linguagem Clojure.

O grupo de trabalho foi formado por Geovanna Araújo e Haama Kethelen.

## Comunicação entre tarefas em Clojure.

### Informações gerais

> Qual o objetivo de comunicação entre tarefas? 
Atender vários usuários simultâneos: evita esperas longas e cansativas para o usuário;
Uso de computadores multiprocessador: a divisão de tarefas aumenta a velocidade de execução de uma aplicação;
Modularidade: sistemas grandes e complexos tem suas tarefas divididas em módulos para melhor oganização;
Construção de aplicações interativas: em aplicações com alta interatividade, tarefas associadas à
interface reagem a comandos do usuário, enquanto outras tarefas comunicam
através da rede.

> Porque usar docker nesse trabalho?
Usamos docker porque ele traz vantagens como simulação realista de ambientes distribuídos, facilidade de execução e avaliação e gestão de dependência de portas.

> Qual a configuração do docker?
Dockerfile e Docker Compose.

### Comunicação entre tarefas com linhas de execução no mesmo processo
> > Atribuição de namespace (ns), gerador de dados (produzir-dados), processador de dados (consumir dados), thread Produtora (future), thread Consumidora (future e @) e sincronização Principal (@thread-consumidor).

##Exemplo:
```clojure
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
        thread-consumidor (future
                            (println "### consumir - iniciado")
                            (consumir-dados @thread-produtor)
                            (println "### consumir - terminado"))]
    @thread-consumidor)
  (println "finalizou"))

### Execução:
> explicar como foi executado
> mostrar as saídas do terminal
> mostrar as saídas do terminal

### Problemas na execução:
> se houve problema na execução, enumerar os problemas e suas respectivas soluções

### Comunicação entre tarefas em processos diferentes no mesmo computador

## Resumo do código:
> > Importações do Java (:import) (ServerSocket para executar a porta de rede) e PrintWritter (para enviar dados via texto), abertura do Servidor (ServerSocket.) (abre a porta TCP), gerenciamento de Recursos (with-open) (garante que o socket e as conexões de rede sejam fechado automaticamente assim que a transmissão terminar ou ocorrer erro), serialização e Envio de Dados (pr-str e .println) (converte os dados Clojure em uma Str formatada e envia para o consumidor através de conexão TCP).

```clojure
(ns exemplo.processo-produtor
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

### Execução:
> explicar como foi executado
> mostrar as saídas do terminal
> mostrar as saídas do terminal

### Problemas na execução:
> se houve problema na execução, enumerar os problemas e suas respectivas soluções

### Comunicação entre tarefas em processos diferentes em computadores diferentes

## Resumo do código:
> Importações do Java (:import) (para executar a porta de rede) e PrintWritter (para enviar dados via texto), abertura do Servidor (ServerSocket.) (abre a porta TCP), gerenciamento de Recursos (with-open) (garante que o socket e as conexões de rede sejam fechado automaticamente assim que a transmissão terminar ou ocorrer erro), serialização e Envio de Dados (pr-str e .println) (converte os dados Clojure em uma Str formatada e envia para o consumidor através de conexão TCP).
Exemplo:

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

### Execução:
> explicar como foi executado
> mostrar as saídas do terminal
> mostrar as saídas do terminal

### Problemas na execução:
> se houve problema na execução, enumerar os problemas e suas respectivas soluções


## Considerações finais

FIXME
> conseguiu implementar tudo e executar?
> qual foi o aprendizado nesse trabalho?
> alguma recomendação para próximos alunos?
