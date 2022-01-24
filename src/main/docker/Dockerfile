FROM centos:centos7

MAINTAINER Mathias Schilling <m@matchilling.com>

ENV JAVA_VERSION="1.8.0-openjdk"

RUN yum -y update && \
    yum -y install "java-${JAVA_VERSION}" && \
    yum clean all

ARG JAR_FILE
COPY ${JAR_FILE} app.jar

CMD ["sh","-c","java $JAVA_OPTS -Dserver.port=$PORT -Xms256m -Xmx256m -Xss512k -XX:CICompilerCount=2 -Dfile.encoding=UTF-8 -XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom -jar /app.jar"]