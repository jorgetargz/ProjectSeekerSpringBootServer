FROM openjdk:17-oracle

LABEL mentainer="jmartin44@educa.madrid.org"

WORKDIR /app

EXPOSE 8090

COPY target/ProjectSeekerSpringBoot-0.0.1-SNAPSHOT.jar /app/project-seeker.jar

ENTRYPOINT ["java", "-jar", "project-seeker.jar"]