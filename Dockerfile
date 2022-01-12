FROM registry.access.redhat.com/ubi8/ubi-minimal:8.4-210 as builder

RUN microdnf install wget tar make java-11-openjdk-devel-11.0.13.0.8-3.el8_5.x86_64 git  

RUN wget https://www.apache.org/dist/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz -P /tmp \
    && tar xf /tmp/apache-maven-3.6.3-bin.tar.gz -C /opt \
    && ln -s /opt/apache-maven-3.6.3 /opt/maven



RUN mkdir -p /workspace/common/target/classes \
    && mkdir -p /workspace/spi/target/classes \
    && mkdir -p /workspace/tenant-manager-client/target/classes \ 
    && mkdir -p /workspace/account-management-service/target/classes \
    && mkdir -p /workspace/core/target/classes

WORKDIR /workspace
COPY / /workspace/

RUN export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-11.0.13.0.8-3.el8_5.x86_64 \
    && export M2_HOME=/opt/maven \
    && export MAVEN_HOME=/opt/maven \
    && export PATH=$M2_HOME/bin:$PATH \
    && make build-project

FROM registry.access.redhat.com/ubi8/ubi-minimal:8.3 

ARG JAVA_PACKAGE=java-11-openjdk-headless
ARG RUN_JAVA_VERSION=1.3.8
ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en'
# Install java and the run-java script
# Also set up permissions for user `1001`
RUN microdnf install curl ca-certificates ${JAVA_PACKAGE} \
    && microdnf update \
    && microdnf clean all \
    && mkdir /deployments \
    && chown 1001 /deployments \
    && chmod "g+rwX" /deployments \
    && chown 1001:root /deployments \
    && curl https://repo1.maven.org/maven2/io/fabric8/run-java-sh/${RUN_JAVA_VERSION}/run-java-sh-${RUN_JAVA_VERSION}-sh.sh -o /deployments/run-java.sh \
    && chown 1001 /deployments/run-java.sh \
    && chmod 540 /deployments/run-java.sh \
    && echo "securerandom.source=file:/dev/urandom" >> /etc/alternatives/jre/lib/security/java.security

# Configure the JAVA_OPTIONS, you can add -XshowSettings:vm to also display the heap size.
ENV JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"

COPY --from=builder /workspace/core/target/lib/*        /deployments/lib/
COPY --from=builder /workspace/core/target/*-runner.jar /deployments/app.jar

EXPOSE 8080
USER 1001

ENTRYPOINT [ "/deployments/run-java.sh" ]