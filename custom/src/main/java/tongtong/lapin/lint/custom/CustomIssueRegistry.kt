package tongtong.lapin.lint.custom

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.Issue
import tongtong.lapin.lint.custom.lssues.xml.CustomXmlAttributeDetector
import tongtong.lapin.lint.custom.lssues.xml.CustomXmlElementDetector

class CustomIssueRegistry: IssueRegistry() {
    override val issues: List<Issue>
        get() = listOf(CustomXmlElementDetector.ISSUE_ATTR, CustomXmlElementDetector.ISSUE_PARENT, CustomXmlAttributeDetector.ISSUE)

}