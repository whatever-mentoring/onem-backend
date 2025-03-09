# onem-backend

whatever 사의 internal-core 팀에 입사하신 것을 축하합니다!

internal-core 팀은 전사 공통 util 서비스를 제공하고 있으며, 당신에게 처음으로 맡겨진 서비스는 그 중 하나인 url-shortener 입니다. url-shortener 서비스를 운영하기 위해 제시된
정보는 아래와 같습니다.

- 완전한 util 서비스로 긴 url 을 짧게 줄여주는 서비스입니다.
- internal vpc 에서 운영되고 있어 인증/인가 로직을 생략 합니다.
- 인프라 스펙은 1 core, 1GB memory, 1 pod 로 운영되고 있습니다.
- 기존 트래픽은 RPM 30 입니다.
- 가끔 버그가 발생하지만 원인을 알 수 없습니다.
- 선임 개발자가 있긴 하지만 너무 바빠 코드 리뷰만 도와줄 수 있습니다.

어려움은 있겠지만, 당신은 서비스의 새로운 오너로서 성공적인 운영을 해나갈 수 있을 것 입니다!

> kotlin 초기 세팅은 `mainKt` branch 에 있습니다.

### guideline

> 개발에 대한 생각은 개발자마다, 심지어 대가들 끼리도 미묘하게 다릅니다.  
> 저의 멘토링 & 코드 리뷰는 당연하게도 저의 지극히 개인적인 관점과 경험에서 진행됩니다.  
> 그렇기에 멘티는 다양한 의견을 듣고 본인에 적합한 피드백을 스스로 선택하기를 기대합니다.
>
> 이 멘토링에서는 버그를 고쳐주거나, 코드를 작성해주지 않습니다.  
> 스스로 학습할 수 있는 키워드와 레퍼런스만을 제공합니다.
>
> 정답은 존재하지 않으며, 베스트 프렉티스는 상황에 따라 다릅니다.

- 업무 요건을 글자 그대로 받아들이지 마세요.
- 사용성 또한 보이지 않는 기술 요건 중 하나 입니다.
- 애플리케이션을 ide 안에서만 기동하려고 하지 마세요.
- 자동화된 테스트가 필요한 시점을 판단하세요.
- 구조화가 필요한 시점을 판단하세요.
- 구현되어 있는 것을 변경할 때 무엇을, 어떤 순서로 변경할지 판단하세요.
- record 를 제외하고 java 11 을 넘어가는 문법은 사용을 지양해주세요.

### how to run

1. application build
    ```shell
    ./gradlew bootJar
    ```

2. application run

    ```shell
    java -jar build/libs/onem-backend-0.0.1-SNAPSHOT.jar
    ````

3. create shorten-url key

   ```shell
   curl -X POST --location "http://localhost:8080/shorten-url" \
    -H "Content-Type: application/json" \
    -d '{
      "originUrl" : "https://www.google.com"
    }'
   ```

4. search shorten-url by created key

    ```shell
    curl -X GET --location "http://localhost:8080/shorten-url?key=4888333" \
        -H "Accept: application/json"
    ```
