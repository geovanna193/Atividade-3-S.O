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

##Exemplo main:
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

(-main) ;ch
### Execução:
> explicar como foi executado: docker compose run --rm --build mesmo-processo 
> mostrar as saídas do terminal (testes): Imagens_terminal/codigo1/teste1cod11.png
> mostrar as saídas do terminal (resultado): Imagens_terminal/resultadocod1.png

### Problemas na execução:
> se houve problema na execução, enumerar os problemas e suas respectivas soluções:

Problema 1: A construção da imagem Docker falhou pois houve um erro no download da imagem base do Clojure, provavelmente indisponibilidade de tag ou oscilação de rede.
Solução: Alterar o arquivo Dockerfile () para (FROM clojure:temurin-17-tools-deps-alpine) que muda a imagem base do Dockerfile para uma versão de suporte oficial mais estável. Funcionou? Não, a imagem foi construída mas o código deu erro clojure na última linha.

Problema 2: Divergência de nomenclatura no YAML com a chamada pelo terminal.
Solução: Modificar o arquivo docker-compose.yml para chamar a função principal no comando -e. funcionou? Não, o container é encerrado em silêncio depois da criação e não tem saída.

Problema 3: Não há print na saída, falha na chamada da main.
solução: Atualizar deps.edn, yaml e adicionar (-main) dentro do exemplo_main.clj para garantir a chamada da função. Funcionou? SIM, agora existe uma saída e é a esperada.

### Comunicação entre tarefas em processos diferentes no mesmo computador:

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

### Execução:
> explicar como foi executado: docker compose up --build produtor-local ;up sobe o ecossistema de serviços (contrsói imagem, cria e inicia o contêiner, conecta as reedes e mapeia as portas TCP) diferente do run que só executa uma tarefa pontual/única dentro de um contêiner e encerra.
docker compose down limpa os contêiners.
docker run -rm consumidor-rede para rodar o consumidor.
> mostrar as saídas do terminal (teste):
> mostrar as saídas do terminal (resultado):

### Problemas na execução:
> se houve problema na execução, enumerar os problemas e suas respectivas soluções:
Problema 1: O clojure não executou o namespace correto ou usou o caminho classpath.
Solução: Ajustar o arquivo yml para usar a chamada namespace nativa. Funcionou? N~ão, houve falha na JVM/Clojure ao tentar encontrar o código do aplicativo e o Clojure não encontrou produtor-consumidor dentro das pastas de configuração no classpath.

Problema 2: Mapeamento da estrutura de diretórios classpath.
Solução:Verificar e alterar deps.edn, docker-compose.yml. (adicionar /clojure em "src" em paths:) Funcionou? Parcialmente, o produtor funciona mas o consumidor não.

Problema 3: Ambos os terminais tentaram usar as mesmas portas simultânemente.
SOlução: Compose down e depois up, no terminal da direita (consumidor) utilizar o comando docker run --rm --network host atividade-3-so-produtor-local clj -M -m produtor-consumidor localhost
Funcionou? Não, ainda existe problema quanto ao uso das portas.

Problema 4: A estrutura do código produtor_consumidor não estava coerente no consumidor.
Solução:Reajuste no código src produtor_consumidor. Funcionou? Em partes, agora há problemas com o endereço.

Problema 5: O parâmetro localhost foi interpretado como argumento do -m no comando (docker compose run --rm --network host produtor-local clj -M -m produtor-consumidor localhost)
Solução: Usar (docker compose run --rm -v $(pwd):/usr/src/app produtor-local clj -M -m produtor-consumidor produtor-local) Funcionou? Não, os argumentos do docker-compose.yml não foram repassados corretamente.

Problema 6: Tentativa de resolver o problema 5.
Solução: docker compose run --rm --entrypoint "clj -M -m produtor-consumidor produtor-local" produtor-local Funcionou? Não..

Nenhuma outra tentativa foi feita para resolver o problema 6.

### Comunicação entre tarefas em processos diferentes em computadores diferentes:

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
> explicar como foi executado: docker compose up --build produtor-rede consumidor-rede
> mostrar as saídas do terminal (testes):
> mostrar as saídas do terminal (resultado):

### Problemas na execução:
> se houve problema na execução, enumerar os problemas e suas respectivas soluções:
Problema 1: O serviço consumidor-rede não subiu em modo consumidor, só em produtor.
Solução: Verificar e atualizar o produtor_consumidor.yml Funcionou? Não, o arquivo consumidor não está sendo chamado corretamente.

Problema 2: Ainda tentando solucionar o problema 1.
Solução: alterar o .yaml e definir o command em formato de array JSON. Funcionou? Não, deu load error in parser (sintaxe).

Problema 3: Erro de sintaxe no YAML, o array JSON faz com que o docker execute o comando no modo exec direto sem sub shell (sh -c)
Solução: Juste no código YAML. Funcionou? Não, o processo produtor começou mas não houve print.

Nenhum outro teste foi realizado.



## Considerações finais

FIXME
> conseguiu implementar tudo e executar? Não, apenas o primeiro código foi 100% implementado.
> qual foi o aprendizado nesse trabalho? Clojure é uma linguagem particular e pode ser bem rígida quanto a sdua sintaxe, aprendi um pouco mais sobre gerenciamento de procesos, gestão de portas, hostname virtual, isolamento e contrato de serviços (contâiners).
> alguma recomendação para próximos alunos? Estudem bem a sintaxe da linguagem escolhida bem como a estrutura de concorrência e Threads.  
