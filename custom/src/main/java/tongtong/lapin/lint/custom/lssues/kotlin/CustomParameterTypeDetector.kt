package tongtong.lapin.lint.custom.lssues.kotlin

import com.android.tools.lint.client.api.JavaEvaluator
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.TextFormat
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiType
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UExpression

class CustomParameterTypeDetector : Detector(), Detector.UastScanner {
    companion object {
        val ISSUE: Issue = Issue.create(
            "ParameterType",
            "Parameter as Activity can be dangerous",
            "Be careful, Activity as Parameter can be dangerous. Are you checked cancel method befor this?",
            Category.COMPLIANCE,
            5,
            Severity.WARNING,
            Implementation(
                CustomParameterTypeDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }

    override fun getApplicableMethodNames(): List<String>? {
        return listOf("run")
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        val evaluator = context.evaluator
        val isMember = evaluator.isMemberInClass(
            method,
            "tongtong.lapin.lint.exemple.examples.Example6"
        )

        if (isMember) {
            detectParameter(node.valueArguments, "android.app.Activity", evaluator).takeIf { it }
                ?.let {
                    context.report(
                        ISSUE,
                        node,
                        context.getNameLocation(node),
                        ISSUE.getExplanation(TextFormat.TEXT)
                    )
                }
        }
    }

    private fun detectParameter(
        argumentList: List<UExpression>,
        superClass: String,
        evaluator: JavaEvaluator
    ): Boolean {
        for (argument in argumentList) {
            argument.getExpressionType()?.let {
                if (isExtendedType(it, superClass, evaluator)) {
                    return true
                }
            }
        }
        return false
    }

    private fun isExtendedType(
        expressionType: PsiType,
        superClass: String,
        evaluator: JavaEvaluator
    ): Boolean {
        return evaluator.run {
            extendsClass(
                getTypeClass(expressionType),
                superClass,
                false
            )

        }
    }

}