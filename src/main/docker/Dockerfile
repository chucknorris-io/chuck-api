FROM azul/zulu-openjdk-alpine:17.0.4.1-17.36.17-jre-headless

ARG JAR_FILE
COPY ${JAR_FILE} app.jar

CMD ["sh","-c","java $JAVA_OPTS -Dserver.port=$PORT -Xms192m -Xmx192m -Xss512k -XX:CICompilerCount=2 -Dfile.encoding=UTF-8 -XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom -jar /app.jar"]