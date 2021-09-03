FROM maven:3.6.3-openjdk-11-slim AS builder
WORKDIR /opt/app
COPY ./pom.xml /opt/app
RUN mvn dependency:go-offline -B --fail-never
COPY . /opt/app
RUN mvn package -DskipTests
