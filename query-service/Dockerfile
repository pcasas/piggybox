FROM openjdk:8-jre-alpine
MAINTAINER Pau Casas Prunés <admin@code.parts>
EXPOSE 5052
COPY build/distributions/query-service.zip /opt/
RUN unzip /opt/query-service.zip -d /opt
RUN apk update && apk add bash
