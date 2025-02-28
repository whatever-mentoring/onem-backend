## build stage
#FROM gradle:8.5-jdk21 AS build
#
#WORKDIR /app
#
#COPY build.gradle.kts settings.gradle.kts gradlew ./
#
#COPY gradle/ gradle/
#
#RUN gradle dependencies --no-daemon
#
#COPY src ./src
#
#RUN ./gradlew clean build --no-daemon
#
#
## RUNTIME
#FROM amazoncorretto:23-alpine3.21-jdk
#
#WORKDIR /app
#
#COPY --from=build app/build/libs/*.jar /app/app.jar
#
#ENTRYPOINT ["java"]
#CMD ["-jar","app.jar"]
#
#EXPOSE 8080




### 편의를 위해(시간이 너무 오래걸려서 답답함)
FROM amazoncorretto:23-alpine3.21-jdk
COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]