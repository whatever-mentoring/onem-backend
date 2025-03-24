#!/bin/bash

# TestContainers 환경 변수 설정
export DOCKER_HOST="unix:///Users/jeongseongheon/.rd/docker.sock"
export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE="/Users/jeongseongheon/.rd/docker.sock"
export TESTCONTAINERS_RYUK_DISABLED="true"

# 기본 테스트 클래스 설정 (인자가 없을 경우 BlockedDomainRepositoryTestWithContainers 사용)
TEST_CLASS=${1:-"BlockedDomainRepositoryTestWithContainers"}

echo "🚀 TestContainers 테스트 실행: $TEST_CLASS"
./gradlew clean test --tests $TEST_CLASS

# 종료 코드 확인
if [ $? -eq 0 ]; then
  echo "✅ 테스트 성공!"
else
  echo "❌ 테스트 실패!"
fi
