package tongtong.lapin.lint.exemple.examples

import android.content.Context

class Example4 {
    fun set() {
        Setter().apply {
            val sampleInt = 1234
            setObjet(SampleObject("SampleObject"))
            setIntValue(sampleInt)
            setStringValue("StringValue")
            setFloatValue(2.22f)
            setDoubleValue(22.22)
            setList(listOf("stringList1", "stringList2"))
            setArray(arrayOf("stringArray1", "stringArray2"))
        }
    }
}

// Parameters are checked By CustomParameterDetector if each type is Characters, Numbers or Array
class Setter {
    fun setObjet(sampleObjet: SampleObject) {

    }

    fun setIntValue(intValue: Int) {

    }

    fun setStringValue(stringValue: String) {

    }

    fun setList(list : List<String>) {

    }

    fun setArray(array: Array<String>) {

    }

    fun setFloatValue(value: Float) {

    }

    fun setDoubleValue(value : Double) {

    }
}

data class SampleObject(val value: String) {
    override fun toString(): String {
        return "This is SampleObject"
    }
}