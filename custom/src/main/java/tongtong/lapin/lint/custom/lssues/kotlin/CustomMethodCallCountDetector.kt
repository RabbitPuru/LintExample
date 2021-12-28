package tongtong.lapin.lint.custom.lssues.kotlin

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement

class CustomMethodCallCountDetector: Detector(), Detector.UastScanner {
    companion object {
        val ISSUE: Issue = Issue.create(
            "DuplicatedCalling",
            "`tongtong.lapin.lint.exemple.ExampleWorker.run()` is called too much.",
            "`tongtong.lapin.lint.exemple.ExampleWorker.run()` is called too much. It had to called once.",
            Category.USABILITY,
            1,
            Severity.ERROR,
            Implementation(
                CustomMethodCallCountDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }

    private var count = 0

    override fun getApplicableMethodNames(): List<String>? {
        return listOf("run")
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        super.visitMethodCall(context, node, method)
        val evaluator = context.evaluator

        if (evaluator.isMemberInClass(method, "tongtong.lapin.lint.exemple.ExampleWorker")) {
            count += 1
        }
    }

    override fun afterCheckFile(context: Context) {
        if (count > 1) {
            context.report(
                ISSUE,
                Location.create(context.file),
                ISSUE.getExplanation(TextFormat.TEXT)
            )
            // If you want to check method call's count by file
            // you should set the count '0'.
            count = 0
        }
    }
}