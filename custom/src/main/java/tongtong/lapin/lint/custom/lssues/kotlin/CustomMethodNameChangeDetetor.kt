package tongtong.lapin.lint.custom.lssues.kotlin

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.LintFix
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.TextFormat
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiMethodCallExpression
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.getParentOfType

class CustomMethodNameChangeDetetor : Detector(), Detector.UastScanner {
    companion object {
        val ISSUE: Issue = Issue.create(
            "RenameMethod",
            "`methodNameBefore` should be renamed.",
            "`methodNameBefore` should be renamed to `methodNameAfter`.",
            Category.CORRECTNESS,
            1,
            Severity.WARNING,
            Implementation(
                CustomMethodNameChangeDetetor::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }


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
                "tongtong.lapin.lint.exemple.examples.Example5"
            )
        ) {
            val quickfixData = LintFix.create()
                .name("change to `methodNameAfter`")
                .replace()
                .text(node.name)
                .with("methodNameAfter")
                .build()

            context.report(
                CustomMethodNameChangeDetetor.ISSUE,
                node,
                context.getNameLocation(node),
                CustomMethodNameChangeDetetor.ISSUE.getExplanation(TextFormat.TEXT),
                quickfixData
            )
        }
    }
}