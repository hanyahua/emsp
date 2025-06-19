FROM eclipse-temurin:21-jdk
COPY emsp.jar /emsp.jar
ENTRYPOINT ["java", "-jar", "/emsp.jar"]
CMD ["--spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]
