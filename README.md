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



Lint 프로젝트는 크게 다음 구성요소로 구성 됩니다.

- IssueRegistry
- Issue
- Detector



### Issue

lint 체크가 정의하는 문제의 타입이나 클래스 입니다. Issue는 관련된 severity(error, warning 혹은 info), 우선순위, 카테고리, 설명 등을 가지고 있습니다. 

Issue는 별도로 클래스 상속 없이 factory방식으로 만들어집니다.

```kotlin
val ISSUE: Issue = Issue.create(
    Id,
    BriefDescription,
    Explanation,
    Category,
    Priority,
    Severity,
    Implementation
)
```

ID : 이슈를 정의하는 읽을 수 있으며 고유한 ID입니다. 대문자로 시작하는 Camel case방식으로 써야 합니다. (예시 : MyIssueId)

BriefDescription : 이슈에 대한 약 5-6 단어 정도 혹은 그보다 더 적은 양의 짧은 요약입니다. 보통 문제 자체만을 설명합니다. (예시 : Missing minSdkVersion)

Explanation : 이슈에 대한 전체적인 설명입니다. 어떻게 수정해야 하는지, 자세한건 어디서 봐야 하는지 등을 포함합니다.

Category : 이슈의 카테고리 입니다. [Categoty 클래스 설명](https://www.javadoc.io/static/com.android.tools.lint/lint-api/23.2.8/com/android/tools/lint/detector/api/Category.html)을 참고 하시면 됩니다. 추후에 린트 결과를 볼 때 해당 이슈를 어디에 분류할지 결정 합니다.

Priority : 이슈의 중요도 입니다. 1~10 으로 되어 있으며, 10이 제일 큽니다. 분류된 리스트 내에서 어떤 이슈가 제일 위에 가는지 결정 합니다.

Severity : 이슈의 심각도이며, 본 프로젝트의 빌드에 영향을 끼치는 요소입니다. 다음으로 구성되어 있습니다.

- IGNORE : 유저는 이 이슈를 보기를 원하지 않습니다. 해당 이슈는 직접 노출되지 않습니다.
- INFORMAL : 문제가 될것 같지 않지만, 코드에 대해서 뭔가 말하고 싶을때 작성합니다.
- WARNING : 아마도 문제가 될 만한 이슈입니다.
- ERROR : 수정되어야 할 실제 에러로 알려진 이슈 입니다. (빌드 불가)
- FATAL : ERROR보다 더 심한 ISSUE입니다. 치명적으로 표시된 경고는 심각한 것으로 간주되고 빌드를 중단하기 때문에 드물게 사용해야 합니다. (빌드 불가)

Implementation : Issue가 어떻게 탐지되는지, 무엇을 탐지하는지 정의합니다. 이슈를 어떻게 탐지하는지는 Detector 클래스를 지정하고, 무엇을 탐지하는지는 Scope를 통해서 지정합니다. [Scope의 범위](https://www.javadoc.io/doc/com.android.tools.lint/lint-api/25.3.0/com/android/tools/lint/detector/api/Scope.html)는 다음과 같습니다.

- JAVA_FILE_SCOPE : JAVA파일과 Kotlin파일을 대상으로 탐색합니다
- ALL_RESOURCES_SCOPE : 레이아웃, 리소스 등을 대상으로 탐지합니다.
- MANIFEST_SCOPE : AndroidManifest파일을 대상으로 탐색합니다
- GRADLE_SCOPE : gradle 파일을 대상으로 탐색합니다
- CLASS_FILE_SCOPE : 바이트코드로 된 빌드된 클래스를 대상으로 탐색합니다.



## IssueRegistry

```kotlin
class CustomIssueRegistry : IssueRegistry() {
    override val issues: List<Issue>
        get() = listOf(
            Issue1,
          	Issue2
        )

}
```

수행할 이슈 목록을 제공하는 레지스트리 입니다. 작성된 이슈들을 체크하려면 여기에 리스트로 등록해야 합니다.



```gradle
jar {
    manifest {
        attributes("Lint-Registry-v2": "your.package.CustomIssueRegistry")
    }
}
```

작성한 IssueRegistry는 커스텀 Lint의 gradle 파일에 위와 같은 형식으로 등록해줍니다.



## Detector

Detector은 특정 문제를 찾을 수 있습니다. 각 문제 유형은 Issue로 고유하게 식별이 됩니다.

Detector클래스와 탐지하려는 타입 별 Scanner Interface를 상속 받습니다. 자바/코틀린 코드면 Detector.UastScanner을 상속받으며, 매니페스트와 xml리소스의 경우 Detector.XmlScanner을 상속 받습니다.

공통적인 요소는 다음과 같습니다.

- before/afterCheckFile : 한 파일을 모두 체크하기 전후로 호출되는 콜백입니다.
- before/afterCheckEachProject : 한 프로젝트들을 모두 체크하기 전후로 호출되는 콜백입니다.
- before/afterCheckRootProject : Root프로젝트를 체크하기 전후로 호출되는 콜백입니다.



이슈가 발생을 하면 콜백을 할 때 parameter로 오는 context에서 report를 호출하여 이슈를 lint에 보고합니다.

```kotlin
context.report(
    issue = ISSUE,
    scope = element,
    location = context.getNameLocation(element),
    message = ISSUE.getExplanation(TextFormat.TEXT)
)
```

scope에 따라서 IDE에 표시되기도 하고, Lint 체크 결과에만 표시되기도 하는데요, 코드의 특정 요소(메소드, xml element, xml attribue)를 범위로 잡은 경우에는 IDE에 노출되어 개발 하면서 확인할 수 있습니다.

또한 Android Studio기준으로 Alt+Enter을 눌러서 제안되는 내용으로 즉시 고칠 수 있게 하는 QuickFix도 있는데요,

```kotlin
LintFix.create()
			 .name("change to `methodNameAfter`")
			 .replace()
			 .text(node.name)
			 .with("methodNameAfter")
			 .reformat(true)
			 .build()
```

name에 간단한 설명을 추가하고, text에 to-be 텍스트, with에 as-is 텍스트를 넣어주고 replace라고 바꿔주겠다고 설정을 하면 됩니다. reformat(true)를 추가 하면, 텍스트를 수정하고 나서 에디터의 룰에 맞춰서 포맷이 맞춰집니다(Code > Reformat Code와 동일). 그리고 issue를 report할 때 fix를 추가해주면 됩니다. 

```kotlin
context.report(
    issue = ISSUE,
    scope = element,
    location = context.getNameLocation(element),
    message = ISSUE.getExplanation(TextFormat.TEXT),
    quickfixData = fixData
)
```



### XmlScanner 활용

매니페스트와 xml리소스를 탐지할 때 사용됩니다.

xml에서 볼 수 있는 구성 요소들 중에 element와 attribute가 있는데요,

```xml
<ConstraintLayout>
	<TextView
   android:text = "Hello World!"/>
</ConstraintLayout>
```

여기서 ConstraintLayout, TextView는 element에 해당하고요, android:text는 attribute에 해당합니다.

XmlScanner에서 상속받는 메소드들은 다음과 같습니다.

- appliesTo : 방문할 XML의 종류를 선택합니다. LAYOUT, DRAWABLE, COLOR 등 탐색할 XML타입을 지정하게 됩니다.

```kotlin
override fun appliesTo(folderType: ResourceFolderType): Boolean {
    return folderType == ResourceFolderType.LAYOUT
}
```

- getApplicableElements/Attributes : 체크할 element/attribute의 이름을 지정합니다. 리스트 형태로 return합니다.

```kotlin
override fun getApplicableAttributes(): Collection<String>? {
    return listOf(
        "layout_constraintLeft_toLeftOf",
        "layout_constraintLeft_toRightOf",
        "layout_constraintRight_toRightOf",
        "layout_constraintRight_toLeftOf"
    )
}
```

- visitElement/Attribute : appliesTo에서 지정한 xml타입과 getApplicable에서 지정한 요소를 방문합니다.
  - 해당 메소드에서 element의 attribute를 찾을 때에는 NameSpace를 구분해야 합니다. 예를 들어 단순히 `element.hasAttribute("src")`를 실행하면 `android:src`와 `tools:src`를 구분할 수 없습니다. 그래서 `element.hasAttributeNS("http://schemas.android.com/apk/res/android", "src")`로 구분을 해야 합니다.



### UsatScanner 활용

자바/코틀린 코드를 탐지할 때 사용 됩니다. 자바/코틀린 코드는 내부적으로 USAT(Universal Abstract Syntax Tree)라는 트리로 코드가 재구성 되어 scanner가 해당 트리를 기준으로 코드를 탐색하므로, 자바, 코틀린 각각을 탐지하기 위해 두개의 Detector을 만들 필요가 없습니다. xml의 경우 처럼 탐지하려는 것에 따라 다른 메소드를 사용하게 됩니다.

**메소드 콜을 탐지**

```kotlin
class CustomMethodCallDetector : Detector(), Detector.UastScanner {

    override fun getApplicableMethodNames(): List<String>? {
        return listOf("deprecatedFunction")
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        val evaluator = context.evaluator

        if (evaluator.isMemberInClass(method, "your.package.CustomClass")) {
           
            context.report(
                ISSUE,
                node,
                context.getNameLocation(node),
                ISSUE.getExplanation(TextFormat.TEXT)
            )
        }
    }
}
```

- getApplicableMethodNames
  - 탐지할 메소드 이름을 호출 합니다.
- visitMethodCall
  - 탐지된 메소드를 호출하는 곳에 방문하여 체크 합니다.
- context.evaluator.isMemberInClass 
  - 해당 메소드가 어떤 클래스의 멤버인지 체크 합니다.



**클래스를 탐지**

```kotlin
class CustomClassDetector : Detector(), Detector.UastScanner {
   
    override fun applicableSuperClasses(): List<String>? {
        return listOf("your.package.ExampleInterface")
    }

    override fun visitClass(context: JavaContext, declaration: UClass) {
        if (context.evaluator.extendsClass(
                declaration.javaPsi,
                "your.package.ExampleInterface",
                false
            )
        ) {
            correct = true
        }
    }
}
```

- applicableSuperClasses
  - 찾으려는 클래스의 부모 클래스/인터페이스를 지정 합니다.
- visitClass
  - 조건에 맞는 클래스를 방문 합니다.
  - context.evaluator.extendsClass
    - applicableSuperClasses에서 지정한 클래스/인터페이스가 아닌 다른 인터페이스/클래스를 상속받았는지 체크 할 수 있습니다.



**메소드선언부 탐지**

```kotlin
class CustomMethodNameChangeDetetor : Detector(), Detector.UastScanner {
    override fun getApplicableUastTypes(): List<Class<out UElement>>? {
        return listOf(UMethod::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return CustomUastHandler(context)
    }
}

class CustomUastHandler(private val context: JavaContext) : UElementHandler() {
    override fun visitMethod(node: UMethod) {
        val evaluator = context.evaluator

        if (node.name == "methodNameBefore" && evaluator.isMemberInClass(
                node,
                "your.package.CustomClass"
            )
        ) {

            context.report(
                ISSUE,
                node,
                context.getNameLocation(node),
                ISSUE.getExplanation(TextFormat.TEXT)
            )
        }
    }
}
```



- getApplicableUastTypes : 찾으려는 UastType을 지정 합니다. UMethod, UClass, UFile, UClass 등이 있습니다.
- 메소드 선언부의 경우 UastHandler을 통해서 찾아야 합니다. 그래서 Custom한 UastHandler을 구현 하였습니다.
  - 방문하게 되는 visitMethod에서 조건을 지정하여 찾고자 하는 메소드를 처리합니다.



## Reference

- [Custom Lint Api Guide](https://googlesamples.github.io/android-custom-lint-rules/api-guide.html)

- [KotlinConf 2017 - Kotlin Static Analysis with Android Lint by Tor Norbye](https://www.youtube.com/watch?v=p8yX5-lPS6o)
