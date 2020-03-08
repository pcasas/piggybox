![Build](https://github.com/pcasas/piggybox/workflows/Build/badge.svg)
[![codecov](https://codecov.io/gh/casasprunes/piggybox/branch/master/graph/badge.svg)](https://codecov.io/gh/casasprunes/piggybox)

# Piggy Box
This is a proof of concept project, which demonstrates [Microservices](http://martinfowler.com/microservices/) with [CQRS](https://martinfowler.com/bliki/CQRS.html) using Ratpack, Kafka and Docker.
It's a simple online wallet, with similar functionalities to the Steam Wallet, you can add funds, buy a game, refund a game, etc.

## Services
Piggy Box is divided into 5 microservices, grouped together in a monorepo but independently deployable. 

<img width="880" alt="Kafka Microservices" src="/diagrams/kafka-microservices.png">

#### Command Service
Using Command Query Responsibility Segregation (CQRS) this service is the way in for commands into our Bounded Context. Uses a Web API which is a collection of HTTP RPC-style methods. For every API call sends commands to different Kafka topics.

HTTP Method	| Method URL | Description
------------- | ------------------------- | ------------- |
POST | /api/preferences.create | Create new preferences
POST | /api/balance.addFunds | Add funds to the customer's balance

#### Query Service
Using Command Query Responsibility Segregation (CQRS) this service is the way in for queries into our Bounded Context. Uses a Web API which is a collection of HTTP RPC-style methods. Queries a Read Model which is a Kafka Streams State Store.

HTTP Method	| Method URL | Description
------------- | ------------------------- | ------------- |
GET | /api/customers.getBalance | Get current customer's balance

#### Preferences Service
Stores the customer preferences, for now only contains the currency in which the customer will operate. Also validates that future operations are done in the same currency.

#### Balance Service
Stores and updates the customer balance for every transaction (e.g. Funds added, Game bought, etc.)

#### Statistics Service
Calculates statistics on transactions.
