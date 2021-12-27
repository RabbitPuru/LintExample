package tongtong.lapin.lint.custom.lssues.xml

import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Attr

class CustomXmlAttributeDetector: Detector(), Detector.XmlScanner {
    companion object {
        val ISSUE : Issue = Issue.create(
            "LeftRightMustBeChanged",
            "`left` and `right` must be changed",
            "The words `left` and `right` have to be changed to `start` and `end`",
            Category.CORRECTNESS,
            5,
            Severity.ERROR,
            Implementation(CustomXmlAttributeDetector::class.java, Scope.RESOURCE_FILE_SCOPE)
        )
    }

    override fun appliesTo(folderType: ResourceFolderType): Boolean {
        return folderType == ResourceFolderType.LAYOUT
    }

    override fun getApplicableAttributes(): Collection<String>? {
        return listOf("layout_constraintLeft_toLeftOf", "layout_constraintLeft_toRightOf", "layout_constraintRight_toRightOf", "layout_constraintRight_toLeftOf")
    }

    override fun visitAttribute(context: XmlContext, attribute: Attr) {
        context.report(
            issue = ISSUE,
            scope = attribute,
            location = context.getNameLocation(attribute),
            message = "The words `left` and `right` have to be changed to `start` and `end`"
        )
    }

}