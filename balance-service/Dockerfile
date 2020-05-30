FROM adoptopenjdk/openjdk8:jre8u252-b09-alpine
MAINTAINER Pau Casas Prunés <admin@code.parts>
EXPOSE 5054
COPY build/distributions/balance-service.zip /opt/
RUN unzip /opt/balance-service.zip -d /opt
RUN apk update && apk add bash
