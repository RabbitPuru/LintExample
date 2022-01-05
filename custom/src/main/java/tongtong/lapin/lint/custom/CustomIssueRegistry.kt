package tongtong.lapin.lint.custom

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.Issue
import tongtong.lapin.lint.custom.lssues.kotlin.CustomClassDetector
import tongtong.lapin.lint.custom.lssues.kotlin.CustomMethodCallCountDetector
import tongtong.lapin.lint.custom.lssues.kotlin.CustomMethodCallDetector
import tongtong.lapin.lint.custom.lssues.kotlin.CustomMethodNameChangeDetetor
import tongtong.lapin.lint.custom.lssues.kotlin.CustomParameterDetector
import tongtong.lapin.lint.custom.lssues.xml.CustomXmlAttributeDetector
import tongtong.lapin.lint.custom.lssues.xml.CustomXmlElementDetector

class CustomIssueRegistry : IssueRegistry() {
    override val issues: List<Issue>
        get() = listOf(
            CustomXmlElementDetector.ISSUE_ATTR,
            CustomXmlElementDetector.ISSUE_PARENT,
            CustomXmlAttributeDetector.ISSUE,
            CustomMethodCallDetector.ISSUE,
            CustomClassDetector.ISSUE_OVERRIDE,
            CustomMethodCallCountDetector.ISSUE,
            CustomParameterDetector.ISSUE,
            CustomMethodNameChangeDetetor.ISSUE
        )

}