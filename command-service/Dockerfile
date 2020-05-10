FROM openjdk:8-jre-alpine
MAINTAINER Pau Casas Prunés <admin@code.parts>
EXPOSE 5051
COPY build/distributions/command-service.zip /opt/
RUN unzip /opt/command-service.zip -d /opt
RUN apk update && apk add bash
