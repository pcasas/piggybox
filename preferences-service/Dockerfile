FROM adoptopenjdk/openjdk8:jre8u252-b09-alpine
MAINTAINER Pau Casas Prunés <admin@code.parts>
EXPOSE 5053
COPY build/distributions/preferences-service.zip /opt/
RUN unzip /opt/preferences-service.zip -d /opt
RUN apk update && apk add bash
