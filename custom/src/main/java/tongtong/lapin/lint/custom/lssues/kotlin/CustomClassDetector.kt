package tongtong.lapin.lint.custom.lssues.kotlin

import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UClass

class CustomClassDetector : Detector(), Detector.UastScanner {
    companion object {
        val ISSUE_OVERRIDE: Issue = Issue.create(
            "ExampleInterfaceExists",
            "Child of `ExampleInterface` is exists",
            "Child of `ExampleInterface` is exists.",
            Category.CORRECTNESS,
            6,
            Severity.ERROR,
            Implementation(
                CustomClassDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }

    private var correct = false

    override fun applicableSuperClasses(): List<String>? {
        return listOf("tongtong.lapin.lint.exemple.ExampleInterface")
    }

    override fun visitClass(context: JavaContext, declaration: UClass) {
        if (context.evaluator.extendsClass(
                declaration.javaPsi,
                "tongtong.lapin.lint.exemple.ExampleInterface",
                false
            )
        ) {
            correct = true
        }
    }

    // That report can be showed at lint's result, not IDE Editor.
    override fun afterCheckEachProject(context: Context) {
        if (correct) {
            context.report(
                ISSUE_OVERRIDE,
                Location.create(context.file),
                ISSUE_OVERRIDE.getExplanation(TextFormat.TEXT)
            )
        }
    }
}