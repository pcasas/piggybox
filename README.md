![Build](https://github.com/pcasas/piggybox/workflows/Build/badge.svg)
[![codecov](https://codecov.io/gh/casasprunes/piggybox/branch/master/graph/badge.svg)](https://codecov.io/gh/casasprunes/piggybox)

# Piggy Box
It's a tutorial project to show how [Microservices Architecture Pattern](http://martinfowler.com/microservices/) can be implemented using Ratpack, Kafka and Docker.   
Piggy Box is the Backend of an online wallet [Bounded Context](https://martinfowler.com/bliki/BoundedContext.html) of a digital distribution platform for PC gaming. 

## Service Architecture
Piggy Box is divided into 5 microservices, grouped together in a Monorepo but independently deployable. 

<img width="880" alt="Kafka Microservices" src="/diagrams/kafka-microservices.png">

#### Command Service
Using Command Query Responsibility Segregation (CQRS) this service is the way in for commands into our Bounded Context. Uses a Web API which is a collection of HTTP RPC-style methods. For every API call sends commands to different Kafka topics.

HTTP Method	| Method URL | Description
------------- | ------------------------- | ------------- |
POST | /api/preferences.create | Create new preferences
POST | /api/balance.addFunds | Add funds to the customer's balance
POST | /api/balance.buyGame | Buy a game for a customer

#### Query Service
Using Command Query Responsibility Segregation (CQRS) this service is the way in for queries into our Bounded Context. Uses a Web API which is a collection of HTTP RPC-style methods. Queries a Read Model which is a Kafka Streams State Store.

HTTP Method	| Method URL | Description
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
