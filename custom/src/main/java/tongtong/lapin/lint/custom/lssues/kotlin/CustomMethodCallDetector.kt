package tongtong.lapin.lint.custom.lssues.kotlin

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression

class CustomMethodCallDetector : Detector(), Detector.UastScanner {
    companion object {
        val ISSUE: Issue = Issue.create(
            "DeprecatedMethod",
            "`deprecatedFunction` is Deprecated",
            "`deprecatedFunction` is Deprecated. `suggestedFunction` is suggested.",
            Category.CORRECTNESS,
            6,
            Severity.WARNING,
            Implementation(
                CustomMethodCallDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }

    override fun getApplicableMethodNames(): List<String>? {
        return listOf("deprecatedFunction")
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        val evaluator = context.evaluator

        if (evaluator.isMemberInClass(method, "tongtong.lapin.lint.exemple.CustomClass")) {
            val quickfixData = LintFix.create()
                .name("change to `suggestedFunction`")
                .replace()
                .text(method.name)
                .with("suggestedFunction")
                .build()

            context.report(
                ISSUE,
                node,
                context.getNameLocation(node),
                ISSUE.getExplanation(TextFormat.TEXT),
                quickfixData
            )
        }
    }
}