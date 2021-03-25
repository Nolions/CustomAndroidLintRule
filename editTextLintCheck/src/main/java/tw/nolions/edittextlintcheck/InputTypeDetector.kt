package tw.nolions.edittextlintcheck

import com.android.SdkConstants
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Element

class InputTypeDetector : LayoutDetector() {
    companion object {
        @JvmStatic
        internal val ISSUE_MISSING_INPUT_TYPE = Issue.create(
            id = "MissingInputType",
            briefDescription = "Specify inputType attribute to get proper keyboard shown by system.",
            explanation = "You should specify an inputType for each EditText so that you can get the proper keyboard to be shown by system.",
            category = Category.USABILITY,
            priority = 8,
            severity = Severity.ERROR,
            implementation = Implementation(
                InputTypeDetector::class.java,
                Scope.ALL_RESOURCES_SCOPE
            )
        ).addMoreInfo("https://developer.android.com/training/keyboard-input/style")
    }

    override fun getApplicableElements(): Collection<String>? {
        return listOf(
            SdkConstants.EDIT_TEXT,
            "androidx.appcompat.widget.AppCompatEditText",
            "android.support.v7.widget.AppCompatEditText"
        )
    }

    override fun visitElement(context: XmlContext, element: Element) {

        if (!element.hasAttribute(SdkConstants.ATTR_INPUT_TYPE)) {
            context.report(
                issue = ISSUE_MISSING_INPUT_TYPE,
                location = context.getLocation(element),
                message = ISSUE_MISSING_INPUT_TYPE.getExplanation(TextFormat.TEXT)
            )
        }
    }
}