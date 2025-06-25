#!/bin/sh
if [ -z "$SPRING_PROFILES_ACTIVE" ]; then
  echo "Starting app without active profile"
  exec java -jar /emsp.jar
else
  echo "Starting app with active profile: $SPRING_PROFILES_ACTIVE"
  exec java -jar /emsp.jar --spring.profiles.active="$SPRING_PROFILES_ACTIVE"
fi
