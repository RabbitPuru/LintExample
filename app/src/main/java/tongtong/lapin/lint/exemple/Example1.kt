package tongtong.lapin.lint.exemple

class Example1 {
    fun run() {
        val objet = CustomClass()
        objet.deprecatedFunction()

        val expandedObjet = ExpandedCustomClass()
        expandedObjet.deprecatedFunction()

        val overridedObjet = OverridedCustomClass()
        // if `deprecatedFunction` is overrided, lint cannot check the function's call.
        overridedObjet.deprecatedFunction()

        val anotherObjet = AnotherClass()
        anotherObjet.deprecatedFunction()
    }
}

open class CustomClass {
    open fun deprecatedFunction() {
        // did something... maybe
        // @Deprecation annotation is mostly suggested, but I want to show just how lint works.
    }

    fun suggestedFunction() {
        // do something... maybe
    }
}

class ExpandedCustomClass: CustomClass()

class OverridedCustomClass: CustomClass() {
    override fun deprecatedFunction() {

    }
}

class AnotherClass {
    fun deprecatedFunction() {
        // did something... maybe
    }

    fun suggestedFunction() {
        // do something... maybe
    }
}