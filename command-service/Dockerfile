FROM adoptopenjdk/openjdk8:jre8u252-b09-alpine
MAINTAINER Pau Casas Prunés <admin@code.parts>
EXPOSE 5051
COPY build/distributions/command-service.zip /opt/
RUN unzip /opt/command-service.zip -d /opt
RUN apk update && apk add bash
