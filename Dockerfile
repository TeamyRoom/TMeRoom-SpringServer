FROM openjdk:17-alpine

COPY ./build/libs/TMeRoom-0.0.1-SNAPSHOT.jar /usr/src/myapp/
CMD java -jar /usr/src/myapp/TMeRoom-0.0.1-SNAPSHOT.jar --spring.profiles.active=local --spring.config.additional-location=classpath:config/*/
