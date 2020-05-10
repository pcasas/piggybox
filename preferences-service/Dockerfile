FROM openjdk:8-jre-alpine
MAINTAINER Pau Casas Prunés <admin@code.parts>
EXPOSE 5053
COPY build/distributions/preferences-service.zip /opt/
RUN unzip /opt/preferences-service.zip -d /opt
RUN apk update && apk add bash
