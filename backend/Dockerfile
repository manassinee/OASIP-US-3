FROM maven:3-openjdk-11 AS build

WORKDIR /app

COPY pom.xml .

RUN --mount=type=cache,target=/root/.m2 \
  ["mvn", "-f", "pom.xml", "dependency:go-offline"]

COPY src src

RUN --mount=type=cache,target=/root/.m2 \
  ["mvn", "-f", "pom.xml", "-o", "package"]



FROM ibm-semeru-runtimes:open-11-jre-focal

COPY --from=build /app/target/*.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]