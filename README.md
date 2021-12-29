# Custom Lint

Android Lint Tool은 잠재적인 버그와 최적화 개선을 위하여 프로젝트 소스 파일들을 체크하는 정적인 코드 분석툴 입니다.

Android Studio에서 기본적으로 제공되는 툴도 있으며 [Ktlint](https://ktlint.github.io/) 처럼 권장하는 컨벤션에 맞춘 커스텀된 lint 툴도 있습니다.

이런 툴들은 보통 전반적인 안드로이드/코틀린 개발 시 지켜야할 법칙에 대해서 다루는데요, 개별 프로젝트에 맞춘 lint도 필요할텐데, 어떻게 만들 수 있을까요?



## 용어 알아가기

### Issue

- lint 체크가 정의하는 문제의 타입이나 클래스 입니다. Issue는 관련된 severity(error, warning 혹은 info), 우선순위, 카테고리, 설명 등을 가지고 있습니다. 

### IssueRegistry

- IssueRegistry는 lint에 Issue들의 리스트를 제공해줍니다. 하나 혹은 그 이상의 lint check가 있다면, 이것들을 IssueRegistry에 등록하고, META-INF나 gradle 같은 로더 메카니즘에 IssueRegistry를 등록합니다.

### Detector

- Issue를 등록하고, 코드를 분석하고, Incident를 보고하는 lint check의 구현입니다.

### Incident

- 특정한 장소에서의 Issue의 분명한 발생입니다. 예를 들면 "222번째 라인에서 Exception이 발생" 같은 것 입니다.

### Severity

Incident가 에러인지 아니면 단순 경고인지 그것도 아니면 아무것도 아닌건지(단순한 정보를 나타내 하이라이트) 나타냅니다.  안드로이드에서 사용하는 Severity는 5단계로 최하 IGNORE부터 최악인 FATAL단계 까지 있습니다.

- IGNORE : 유저가 이 이슈를 보기 원하지 않습니다.
- INFORMAL : 정보만을 나타냄. 아마도 문제가 아니지만, 이 체크는 코드에 대해서 말하기 위한 흥미로운 어떤걸 찾아낸겁니다.
- WARNING : 문제가 될 수 있습니다. (빌드는 가능)
- ERROR : 이 이슈는 반드시 해결해야할 실제 오류로 알려져 있습니다. (빌드 불가)
- FATAL : 치명적으로 표시된 경고는 심각한 것으로 간주되어 "Export APK etc in ADT"를 중단하므로 드물게 사용해야 합니다. (빌드 불가)



## Custom Lint 시작하기

먼저 라이브러리 모듈을 만듭니다. 그리고 gradle파일에 다음과 같은 의존성을 추가 합니다. 그리고 뒤에서 다룰 IssueRegistry를 만들었다면, gradle파일에 아래와 같이 등록을 합니다. Copy Reference를 하셔서 전체 패키지명까지 정확히 포함되는 클래스명을 사용하셔야 합니다.

```gradle
apply plugin: 'java-library'
apply plugin: 'kotlin'
apply plugin: 'com.android.lint'

dependencies {
    compileOnly "com.android.tools.lint:lint-api:$lintVersion"
    compileOnly "com.android.tools.lint:lint-checks:$lintVersion"
}

jar {
    manifest {
        attributes("Lint-Registry-v2": "전체 패키지명 + IssueRegistry클래스명")
    }
}
```



Lint의 적용을 받을 모듈에는 다음과 같이 의존성을 추가합니다.

```
dependencies {
    lintChecks project(':모듈명')
}
```





## Reference

- [Custom Lint Api Guide](https://googlesamples.github.io/android-custom-lint-rules/api-guide.html)
