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
- Java 8

## Instalação

- Baixar o projeto no github - https://github.com/arimateaoliveira/authorizer/


```bash
 https://github.com/arimateaoliveira/authorizer/
```


### Executando a aplicação

- Execuatr o comando abaixo:

```bash
java -jar authorizer.jar
```


### Testando a instalação  

# Importar o arquivo wayon.postman_collection.json no Postman

```bash
# Criar conta
{"account": {"active-card": true, "available-limit": 100}}

# Primeira transação - OK
{"transaction": {"merchant": "Burger King", "amount": 50, "time":2021-02-24T10:00:00.000Z"}}

# Segunda transação - OK
{"transaction": {"merchant": "Habbib's", "amount": 30, "time":"2021-02-24T10:00:10.000Z"}}" 

# Terceira transação - OK
{"transaction": {"merchant": "Mc Donalds", "amount": 10, "time":"2021-02-24T10:00:20.000Z"}}" 

# Quarta transação - OK
-- A próxima transação vai violar a duplicidade, saldo e alta frequência de envio
{"transaction": {"merchant": "Burger King", "amount": 50, "time":"2021-02-24T10:00:30.000Z"}}" 

# O H2database pode ser acessado com o user: sa password: password:
# <http://localhost:8080/h2-console> 
```

### Tecnologias

As seguintes ferramentas foram usadas na construção do projeto:

- Java
- Spring boot
- H2Database






