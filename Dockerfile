FROM eclipse-temurin:21-jdk
COPY emps.jar /emps.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
CMD ["--spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]
