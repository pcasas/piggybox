FROM openjdk:8-jre-alpine
MAINTAINER Pau Casas Prunés <admin@code.parts>
EXPOSE 5054
COPY build/distributions/balance-service.zip /opt/
RUN unzip /opt/balance-service.zip -d /opt
RUN apk update && apk add bash
