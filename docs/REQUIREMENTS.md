# 요구 사항

- [x] 인증/인가 로직 필요 없음
- [x] 인프라 스펙은 1 core, 1GB memory, 1 pod
- [x] 긴 url을 짧게 줄여주는 기능
- [x] url 마다 고유한 key를 가지고 있어야 함
- [x] 동일한 url을 저장하면 동일한 key를 반환
- [x] 저장 개수는 무제한
    - 메모리 제한으로 JVM Heap 사이즈 75% 권장(768MB)기준 약 3_145_728개 저장 가능 (이론적인적으로 무제한)
    - 참고:
        - [Setting Heap Size Parameters](https://docs.oracle.com/cd/E13188_01/jrockit/docs81/tuning/config.html#1012706)
        - [JVM Performance Tuning](https://docs.oracle.com/en/graalvm/jdk/21/docs/reference-manual/native-image/optimizations-and-performance/MemoryManagement/#performance-tuning)
- [x] 기존 트래픽은 RPM 30

## 문제점

## To-do

## Done

- [x] 가끔 버그가 발생하지만 원인을 알 수 없음 (p1)
    - [x] 로깅을 추가하여 버그 다시 발생 시 원인을 파악할 수 있도록 함 (p1)
- [x] 인프라 스펙 제한 사항에 맞게 테스트 코드 작성하여 트래픽 테스트 (p1)
    - [x] JMeter를 사용하여 테스트

> 테스트 조건:
>
> |  항목              | 값      |
>|------------------|--------|
>| 유저 수(Users)      | 1000   |
>| 루프 수(Loop Count) | 2      |
>| Ramp-Up 시간       | 60 sec |

> 테스트 결과는 다음과 같습니다:
>
>| 항목               | URL 저장	   | URL 조회    | 	TOTAL   |
>|------------------|-----------|-----------|----------|
>| # Samples	       | 10,000	   | 10,000	   | 20,000   |
>| Average	         | 3 ms	     | 0 ms	     | 2 ms     |
>| Min	             | 0 ms	     | 0 ms	     | 0 ms     |
>| Max	             | 28 ms	    | 12 ms	    | 28 ms    |
>| Std. Dev.	       | 2.69 ms	  | 0.84 ms	  | 2.37 ms  |
>| Error %	         | 0.010%	   | 73.630%	  | 36.820%  |
>| Throughput	      | 12.97914	 | 12.97923	 | 25.95825 |
>| Received KB/sec	 | 2.10	     | 2.28	     | 4.38     |
>| Sent KB/sec	     | 2.84	     | 2.53	     | 5.37     |
>| Avg. Bytes	      | 166.0	    | 179.7	    | 172.9    |

- [x] Exception 처리 정책 미정 (PR #63)
    - [x] RuntimeException을 상속받는 커스텀 예외를 사용
- [x] 라이브러리 사용 정책 미정 (PR #63)
    - [x] 선택근거를 명시하고 사용
- [x] 코드 스타일 가이드 미정 (PR #63)
    - [x] Kotlin 코드 스타일은 [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)을 따름
- [x] 커밋 메시지 규칙 미정 (PR #63)
    - [x] 커밋 메시지는 [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/)을 따름
- [x] 유효하지 않는 key로 요청이 들어오면 에러가 발생하지만, 이를 처리하지 않음 (PR #63)
    - [x] 404 에러를 반환하도록 수정
- [x] 동일한 url를 다시 저장하면 새로운 Key를 생성 (PR #58)
    - [x] 기존 key를 반환하도록 수정
- [x] url 저장 시 중복된 key가 발생하여 기존 url을 덮어씌움 (PR #58)
    - [x] key를 seq로 처리하여 중복을 방지
