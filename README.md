![Build](https://github.com/casasprunes/piggybox/workflows/Build/badge.svg)
[![codecov](https://codecov.io/gh/casasprunes/piggybox/branch/master/graph/badge.svg)](https://codecov.io/gh/casasprunes/piggybox)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://github.com/casasprunes/piggybox/blob/master/LICENSE)

# What is Piggybox?

Piggybox is a tutorial project to show how to create [Microservices](http://martinfowler.com/microservices/) using Ratpack, Kafka and Docker.   
Piggybox implements the Backend of an Online Wallet [Bounded Context](https://martinfowler.com/bliki/BoundedContext.html). 
If you are familiar with video game platforms like [Steam](https://store.steampowered.com/) you'll know they have an Online Wallet where you can add funds and later buy games with those funds. Those are the type of functionalities that are implemented in this project.

## Microservice Architecture

Piggy Box is divided into 4 microservices, grouped together in a Monorepo but independently deployable. 

<img width="880" alt="Kafka Microservices Architecture" src="/diagrams/kafka-microservices-architecture.png">

#### Command Service

The Command Service exposes a Web API that accepts requests over HTTP. The requests take the form of commands that can cause changes on the State Stores owned by our microservices.

<img width="880" alt="CQRS Architecture Kafka Command Service" src="/diagrams/cqrs-architecture-kafka-command-service.png">

The Web API is a collection of HTTP RPC style methods with URLs in the form `/api/METHOD_FAMILY.method`.   
For every API call the Command Service sends a command to the Kafka Cluster.

Method | URL | Description
------------- | ------------------------- | ------------- |
POST | /api/preferences.create | Create preferences for a customer with the currency to be used for all transactions
POST | /api/preferences.changeCountry | Change the customer's country from preferences
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

The Preferences Service stores preferences and performs validations. For instance, if a customer's currency is in EUR it won't allow to add funds in currencies different than EUR.
The preferences are stored in the Preferences topic which is used as an Event Store. The Preferences Authorization topic in the other hand is only used to exchange messages between microservices.

In the following diagram we can see an example of how preferences can be created for a customer. First the client calls the `/preferences.create` endpoint from the Command Service, then the Command Services publishes a command `CreatePreferencesCommand` to the Preferences Authorization Topic. Then the Preferences Service reads the `CreatePreferencesCommand`, checks the State Store to see if preferences already exist for this customer. Then, since no preferences exist for this customer, publishes a `PreferencesCreated` event to the Preferences Topic.

<img width="880" alt="Kafka Event Store Preferences Service" src="/diagrams/kafka-event-store-preferences-service.png">

As a last step, the Preferences Service reads the `PreferencesCreated` event from the Preferences Topic and updates the State Store accordingly. The Preferences Service State Store represents the current state of the customer preferences.

<img width="880" alt="Kafka Event Store Preferences Service 2" src="/diagrams/kafka-event-store-preferences-service-2.png">

#### Balance Service

The Balance Service updates the customer's balance based on transactions like `AddFundsCommand` or `BuyGameCommand`. It also performs validations not allowing a balance lower than 0 or higher than 2000. 
The balance transactions are stored in the Balance topic which is used as an Event Store. The Balance Authorization topic in the other hand is only used to exchange messages between microservices.

In the following diagram we can see an example of how funds can be added to a customer's balance. First the client calls the `/balance.addFunds` endpoint from the Command Service, then the Command Services publishes a command `AddFundsCommand` to the Preferences Authorization Topic. Then the Preferences Service reads the `AddFundsCommand`, checks the State Store to see if funds are in the same currency than the customer's preferences currency. Then, since the currency is the same, publishes an `AddFundsCommand` event to the Balance Authorization Topic. Then the Balance Service reads the `AddFundsCommand`, checks the State Store to see if the balance will be lower than 2000 after adding the funds. Then, since the balance will be lower than 2000 after adding the funds, publishes a `FundsAdded` event to the Balance Topic.

<img width="880" alt="Kafka Event Store Balance Service" src="/diagrams/kafka-event-store-balance-service.png">

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
