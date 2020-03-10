![Build](https://github.com/casasprunes/piggybox/workflows/Build/badge.svg)
[![codecov](https://codecov.io/gh/casasprunes/piggybox/branch/master/graph/badge.svg)](https://codecov.io/gh/casasprunes/piggybox)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://github.com/casasprunes/piggybox/blob/master/LICENSE)

# What is Piggybox?
Piggybox is a tutorial project to show how to create [Microservices](http://martinfowler.com/microservices/) using Ratpack, Kafka and Docker.   
Piggybox implements the Backend of an Online Wallet [Bounded Context](https://martinfowler.com/bliki/BoundedContext.html). 
If you are familiar with video game platforms like [Steam](https://store.steampowered.com/) you'll know they have an Online Wallet where you can add funds and later buy games with those funds. Those are the type of functionalities that are implemented in this project.

## Microservice Architecture
Piggy Box is divided into 5 microservices, grouped together in a Monorepo but independently deployable. 

<img width="880" alt="Kafka Microservices Architecture" src="/diagrams/kafka-microservices-architecture.png">

#### Command Service
The Command Service exposes a Web API that accepts requests over HTTP. The requests take the form of commands that can cause changes on the State Stores owned by our microservices.

<img width="880" alt="CQRS Architecture Kafka Command Service" src="/diagrams/cqrs-architecture-kafka-command-service.png">

The Web API is a collection of HTTP RPC style methods with URLs in the form `/api/METHOD_FAMILY.method`.   
For every API call the Command Service sends a command to the Kafka Cluster.

Method | URL | Description
------------- | ------------------------- | ------------- |
POST | /api/preferences.create | Create preferences for a customer with the currency to be used for all transactions
POST | /api/balance.addFunds | Add funds to the customer's balance
POST | /api/balance.buyGame | Buy a game for a customer

#### Query Service
The Query Service waits for Domain Events to happen and updates the Read Model accordingly. Additionally, exposes a Web API that allows HTTP clients to read the resulting Read Model that was generated from the processed events.
For the Read Model it uses a Kafka Streams State Store. 

<img width="880" alt="CQRS Architecture Kafka Query Service" src="/diagrams/cqrs-architecture-kafka-query-service.png">

The Web API is a collection of HTTP RPC style methods with URLs in the form `/api/METHOD_FAMILY.method`.   
For every API call the Query Service queries the Read Model, which is a persistent key-value store, and returns a JSON response.

Method | URL | Description
------------- | ------------------------- | ------------- |
GET | /api/customers.getBalance | Get the current customer's balance

#### Preferences Service
Stores the customer preferences, for now only contains the currency in which the customer will operate. Also validates that future operations are done in the same currency.

#### Balance Service
Stores and updates the customer balance for every transaction (e.g. Funds added, Game bought, etc.)

#### Statistics Service
Calculates statistics on transactions.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

You need to [install Docker](https://docs.docker.com/install/) and Java 8.

For Java 8, you can [install SDKMAN](https://sdkman.io/install). 

Once SDKMAN is installed, you can get a list of candidate java versions from the terminal:

```
sdk list java
``` 

And install Java 8:

```
sdk install java 8.0.242-zulu
```

The recommended IDE for this project is [IntelliJ IDEA](https://www.jetbrains.com/idea/).

## Running the tests

To run the tests you first need to start the Docker containers for Zookeeper, Kafka and Schema Registry, from the terminal in the root of the project:

```
docker-compose up 
```

Then you can run the tests with:

```
./gradlew build
```

To stop Zookeeper, Kafka and Schema Registry and clean the volumes:

```
docker-compose down -v
```

## Built With

* [Ratpack](https://ratpack.io/) - A set of Java libraries for building scalable HTTP applications.
* [Kafka](https://kafka.apache.org/) - A distributed streaming platform.
* [Docker](https://www.docker.com/) - The container platform.
