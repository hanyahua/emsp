FROM eclipse-temurin:21-jdk
COPY emps.jar /emps.jar
ENTRYPOINT ["java", "-jar", "/emps.jar"]
CMD ["--spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]
