FROM openjdk:15
VOLUME /tmp
ADD ./target/springboot-notificaciones-0.0.1-SNAPSHOT.jar notificaciones.jar
ENTRYPOINT ["java","-jar","/notificaciones.jar"]