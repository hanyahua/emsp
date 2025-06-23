#!/bin/bash
exec > /tmp/init.log 2>&1
set -x

sudo apt update -y
apt install docker.io -y
systemctl start docker

docker run -d -p 8080:8080 \
  -e DB_HOST=${DB_HOST} \
  -e DB_USERNAME=${DB_USERNAME} \
  -e DB_PASSWORD=${DB_PASSWORD} \
  -e DB_NAME=${DB_NAME} \
  -e WORKER_ID=${WORKER_ID} \
  -e SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE} \
  ${DOCKER_IMAGE}