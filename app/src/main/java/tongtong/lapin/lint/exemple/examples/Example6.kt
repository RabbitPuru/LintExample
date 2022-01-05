package tongtong.lapin.lint.exemple.examples

import android.content.Context

class Example6 {
    companion object {
        fun create(context: Context): Example6 {
            return Example6()
        }
    }

    fun addTag(tag: String): Example6 {
        return this
    }

    fun run(context: Context) {

    }

    fun cancel(tag: String) {

    }
}