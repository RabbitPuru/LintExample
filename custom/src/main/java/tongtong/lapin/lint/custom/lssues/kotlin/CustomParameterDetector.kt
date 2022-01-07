package tongtong.lapin.lint.custom.lssues.kotlin

import com.android.tools.lint.client.api.JavaEvaluator
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.ConstantEvaluator
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Location
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.TextFormat
import com.android.tools.lint.detector.api.TypeEvaluator
import com.android.tools.lint.helpers.DefaultJavaEvaluator
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression

class CustomParameterDetector : Detector(), Detector.UastScanner {
    companion object {
        val ISSUE: Issue = Issue.create(
            "DetectedParams",
            "Detected Parameters",
            "Detected Parameters By Lint",
            Category.USABILITY,
            1,
            Severity.INFORMATIONAL,
            Implementation(
                CustomParameterDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }

    override fun getApplicableMethodNames(): List<String>? {
        return listOf("setObjet", "setIntValue", "setStringValue", "setList", "setArray", "setFloatValue", "setDoubleValue")
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        val isMember = context.evaluator.isMemberInClass(method, "tongtong.lapin.lint.exemple.examples.Setter")

        if (isMember) {
            val argument = node.valueArguments[0]
            val value = ConstantEvaluator.evaluate(context, argument)
            val typeEvaluator = TypeEvaluator.evaluate(context, method)
            context.report(
                ISSUE,
                Location.create(context.file),
                "Detected from Example4 : method = ${method.name}, value = ${getValue(value)}, type = ${value?.javaClass?.name}, evaluatedType = ${typeEvaluator?.canonicalText}"
            )

        }

    }

    private fun getValue(objet: Any?): String {
        // if objet's type isn't Number, Character or Array, it's value is null.
        return if (objet is Array<*>) {
            objet.joinToString(",","{","}")
        } else {
            objet.toString()
        }
    }
}