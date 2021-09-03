FROM maven:3.6.3-openjdk-11-slim AS builder
WORKDIR /opt/app
COPY ./pom.xml /opt/app
RUN mvn dependency:go-offline -B --fail-never
COPY . /opt/app
RUN mvn package -DskipTests && java -Djarmode=layertools -jar target/server-0.0.1-SNAPSHOT.jar extract

FROM openjdk:11-jdk-slim
WORKDIR /webapp
COPY --from=builder /opt/app/dependencies/ ./
COPY --from=builder /opt/app/spring-boot-loader ./
COPY --from=builder /opt/app/snapshot-dependencies/ ./
COPY --from=builder /opt/app/application/ ./
CMD ["java", "-Duser.timezone=UTC", "-Xms1g", "-Xmx1g", "org.springframework.boot.loader.JarLauncher"]
