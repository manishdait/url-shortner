FROM openjdk:17-alpine AS build
WORKDIR /app
COPY . .
RUN ./mvnw package -DskipTests

FROM openjdk:17-alpine AS jre
WORKDIR /app
COPY --from=build app/target/url-shortner.jar app.jar
RUN jar -xf app.jar
RUN jdeps --print-module-deps --ignore-missing-deps --multi-release 17 --class-path 'BOOT-INF/lib/*' app.jar > deps.info
RUN jlink --output jre --compress=2 --no-header-files --no-man-pages --add-modules "$(cat deps.info)"

FROM alpine
WORKDIR /app
COPY --from=build app/target/url-shortner.jar app.jar
COPY --from=jre app/jre jre
EXPOSE 8080
ENTRYPOINT [ "jre/bin/java", "-jar", "app.jar", "--spring.profiles.active=local" ]
