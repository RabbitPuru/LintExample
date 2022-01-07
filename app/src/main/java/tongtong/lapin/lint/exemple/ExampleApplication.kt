package tongtong.lapin.lint.exemple

import android.app.Application
import tongtong.lapin.lint.exemple.examples.Example6

class ExampleApplication: Application() {
    private val example5 = Example6.create()
    override fun onCreate() {
        super.onCreate()
        example5.run(this)
    }
}