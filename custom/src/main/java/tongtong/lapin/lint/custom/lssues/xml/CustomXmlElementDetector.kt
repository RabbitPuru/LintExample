package tongtong.lapin.lint.custom.lssues.xml

import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Element

class CustomXmlElementDetector: Detector(), Detector.XmlScanner {
    companion object {
        val ISSUE_ATTR : Issue = Issue.create(
            "ImageViewSrc",
            "ImageView has to have `android:src`",
            "You must add `android:src` to ImageView",
            Category.CORRECTNESS,
            5,
            Severity.ERROR,
            Implementation(CustomXmlElementDetector::class.java, Scope.RESOURCE_FILE_SCOPE)
        )

        val ISSUE_PARENT : Issue = Issue.create(
            "ImageViewParentIncorrect",
            "ImageView fits with `androidx.constraintlayout.widget.ConstraintLayout`",
            "ImageView fits with `androidx.constraintlayout.widget.ConstraintLayout`, you have to move it to `androidx.constraintlayout.widget.ConstraintLayout`",
            Category.CORRECTNESS,
            6,
            Severity.ERROR,
            Implementation(CustomXmlElementDetector::class.java, Scope.RESOURCE_FILE_SCOPE)
        )
    }

    override fun appliesTo(folderType: ResourceFolderType): Boolean {
        return folderType == ResourceFolderType.LAYOUT
    }

    override fun getApplicableElements(): Collection<String>? {
        return listOf("ImageView")
    }



    override fun visitElement(context: XmlContext, element: Element) {
        // You can use `element.hasAttribute("src")`, but for separate `android:src` from `tools:src`, you should use hasAttributeNS
        if (!element.hasAttributeNS("http://schemas.android.com/apk/res/android", "src")) {
            context.report(
                issue = ISSUE_ATTR,
                scope = element,
                location = context.getNameLocation(element),
                message = "You must add `android:src` to ImageView"
            )
        }

        if (element.parentNode.localName != "androidx.constraintlayout.widget.ConstraintLayout") {
            context.report(
                issue = ISSUE_PARENT,
                scope = element,
                location = context.getNameLocation(element),
                message = "ImageView fits with `androidx.constraintlayout.widget.ConstraintLayout`"
            )
        }
    }
}