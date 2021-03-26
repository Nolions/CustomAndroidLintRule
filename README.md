# ExerciseCustomAndroidLintRule

Custom Android Lint rule

## Step by Step

Step1.

建立一個Kotlin/Java Module

> 注意是建立`Kotlin/Java Library`而不是Android Library`

Step2. 新增依賴套件

在`editTextLintCheck`模組下的build.gradle中的dependencies區塊新增以下依賴

```
compileOnly "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72"
compileOnly "com.android.tools.lint:lint-api:27.1.3"
compileOnly "com.android.tools.lint:lint-checks:27.1.3"
```

> kotlin-stdlib-jdk7、lint-api與lint-checks與當下最新的版本分別為1.3.72＆27.1.3

Step3. 新增探測器(detector)

新建一個要作為detector的Class，該Class繼承自`LayoutDetector`

```
class InputTypeDetector : LayoutDetector() {
    ...
}
```

新增並指定一個issue

```
companion object {
    @JvmStatic
    internal val ISSUE_MISSING_INPUT_TYPE = Issue.create(
        id = "MissingInputType",
        briefDescription = "Specify inputType attribute to get proper keyboard shown by system.",
        explanation = "You should specify an inputType for each EditText so that you can get the proper keyboard to be shown by system.",
        category = Category.USABILITY,
        priority = 8,
        severity = Severity.ERROR,
        implementation = Implementation(
            InputTypeDetector::class.java,
            Scope.ALL_RESOURCES_SCOPE
        )
    ).addMoreInfo("https://developer.android.com/training/keyboard-input/style")
}
```

id: 每一個issue都有一個獨立ID，用於讓Android Lint識別

priority: issue顯示時的優先度，最高為10，最低為1。

severity: 回報嚴重層級(FATAL, ERROR, WARNING, INFORMATIONAL, IGNORE)

> FATAL, ERROR皆為錯誤，其中`FATAL`將會導致ADT中斷APK產生

implementation: 註冊實施檢測條件

覆寫 `getApplicableElements`方法，並且指定要探測的元件

```
override fun getApplicableElements(): Collection<String>? {
    return listOf(
        SdkConstants.EDIT_TEXT,
        "androidx.appcompat.widget.AppCompatEditText",
        "android.support.v7.widget.AppCompatEditText"
    )
}
```

覆寫 `visitElement`方法，該方法中將可以指定元素檢查條件

```
override fun visitElement(context: XmlContext, element: Element) {
    if (!element.hasAttribute(SdkConstants.ATTR_INPUT_TYPE)) {
        context.report(
            issue = ISSUE_MISSING_INPUT_TYPE,
            location = context.getLocation(element),
            message = ISSUE_MISSING_INPUT_TYPE.getExplanation(TextFormat.TEXT)
        )
    }
}
```

issue: 指定issue

message: 顯示訊息內容

location: 詳細顯示報告檔案與行數

quickfixData: Quick Fixes option

```
LintFix.create()
    .replace()
    .text(SdkConstants.EDIT_TEXT) // Put the text we're looking to replace
    .with(SdkConstants.TEXT_VIEW) // Put the text we want to replace it with
    .build()
```

Step4.

註冊新增Lint規則(issue)

新增一個繼承自IssueRegistryClass，並將建立issue加入到此屬性列表中

```
class LintRegistry : IssueRegistry() {
    override val api: Int
        get() = CURRENT_API

    override val issues: List<Issue>
        get() = listOf(
            InputTypeDetector.ISSUE_MISSING_INPUT_TYPE
        )
}
```

註冊Issue Registry

在`editTextLintCheck`模組下的build.gradle中新增以下程式碼

```
jar {
    manifest {
        // attributes("Lint-Registry-v2": "<your_issue_registry_package_name>")
        attributes("Lint-Registry-v2": "com.varunbarad.androidlintchecks.LintRegistry")
    }
}
```

Step5.

Use

在App module下的build.gradle新增該Lint Registry module 依賴

```
jar {
    manifest {
        // Format is
        // attributes("Lint-Registry-v2": "<fully-qualified-class-name-of-your-issue-registry>")
        attributes("Lint-Registry-v2": "com.varunbarad.androidlintchecks.LintRegistry")
    }
}
```

執行`lint`

```
./gradlew lint
```

## REFERENCE

1. [Android Lint Framework — An Introduction](https://proandroiddev.com/android-lint-framework-an-introduction-36139deedf8b)

2. [WRITE CUSTOM ANDROID LINT RULE - LAYOUT FILES](https://varunbarad.com/blog/write-custom-android-lint-rule-layout-files)

3. [Enforcing Custom View Usage With Android Lint](https://androidessence.com/enforce-custom-views-with-lint)
