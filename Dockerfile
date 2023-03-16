FROM registry.access.redhat.com/ubi8/openjdk-11 AS builder

WORKDIR /workspace

COPY --chown=185:0 / /workspace/

RUN mvn clean install -Dmaven.javadoc.skip=true --no-transfer-progress -DtrimStackTrace=false -DskipTests=true
q
FROM registry.access.redhat.com/ubi8/openjdk-11:latest

USER 185

COPY --from=builder  /workspace/core/target/*-runner.jar /deployments/app.jar
COPY --from=builder  /workspace/core/target/lib/*        /deployments/lib/

EXPOSE 8080

ENV AB_JOLOKIA_OFF=""
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/app.jar"