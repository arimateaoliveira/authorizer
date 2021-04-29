# Authorize Challenge

Essa aplicação tem como responsabilidade criar contas e autorizar uma transação validando uma lista de regras de violação.

<p align="center">
<a href="#Features">Features</a> • 
<a href="#Pré-requisitos">Pré-requisitos</a> •
<a href="#Instalação">Instalação</a> •
<a href="#tecnologias">Tecnologias</a> •
</p>

### Features
- [x] Cadastro de contas
- [x] Cadastro de transações

### Pré-requisitos
- Java
- Docker

## Instalação

- Copiar o arquivo docker-compose.yml enviado para uma pasta local.

- Executar o comando a seguir para subir o docker com o kafka e o zookeeper.

```bash
 sudo docker-compose -f docker-compose.yml up -d
```
- Executar o comando a seguir para listar o ID dos containers do kafka e do zookeeper

```bash
sudo docker ps
```

- Para entrar no console do kafka copiar o ID do container do kafka e executar no comando a seguir

```bash
sudo docker exec -it [ID] /bin/sh
```

- Criar os tópicos de entrada e saída

```bash
# Entradas
/opt/bitnami/kafka/bin/kafka-topics.sh --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic account.input
/opt/bitnami/kafka/bin/kafka-topics.sh --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic account.output
#Saídas
/opt/bitnami/kafka/bin/kafka-topics.sh --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic transaction.input
/opt/bitnami/kafka/bin/kafka-topics.sh --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic transaction.output
```


```bash
sudo docker exec -it [ID] /bin/sh
```

### Executando a aplicação

- Após a execução do docker com o kafka e o zookeeper rodando, copiar o arquivo do projeto (authorizer.jar) para uma
  pasta local e execuatr o comando abaixo:

```bash
java -jar authorizer.jar
```


### Testando a instalação  

```bash
# Criar conta
echo "{\"account\": {\"active-card\": true, \"available-limit\": 100}}" | /opt/bitnami/kafka/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic account.input > /dev/null

# Primeira transação - OK
echo "{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 50, \"time\":\"2021-02-24T10:00:00.000Z\"}}" | /opt/bitnami/kafka/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic transaction.input > /dev/null

# Segunda transação - OK
echo "{\"transaction\": {\"merchant\": \"Habbib's\", \"amount\": 30, \"time\":\"2021-02-24T10:00:10.000Z\"}}" | /opt/bitnami/kafka/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic transaction.input > /dev/null

# Terceira transação - OK
echo "{\"transaction\": {\"merchant\": \"Mc Donalds\", \"amount\": 10, \"time\":\"2021-02-24T10:00:20.000Z\"}}" | /opt/bitnami/kafka/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic transaction.input > /dev/null

# Quarta transação - OK
-- A próxima transação vai violar a duplicidade, saldo e alta frequência de envio
echo "{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 50, \"time\":\"2021-02-24T10:00:30.000Z\"}}" | /opt/bitnami/kafka/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic transaction.input > /dev/null

# O H2database pode ser acessado com o user: sa password: password:
# <http://localhost:8080/h2-console> 
```

### Tecnologias

As seguintes ferramentas foram usadas na construção do projeto:

- Java
- Spring boot
- Kafka
- H2Database






