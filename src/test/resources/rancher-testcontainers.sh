#!/bin/bash
# Rancher Desktop에서 TestContainers를 사용하기 위한 스크립트
export DOCKER_HOST=unix:///Users/jeongseongheon/.rd/docker.sock
export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/Users/jeongseongheon/.rd/docker.sock
export DOCKER_SOCK=/Users/jeongseongheon/.rd/docker.sock

# 테스트 실행
./gradlew test --tests BlockedDomainRepositoryTestWithContainers
