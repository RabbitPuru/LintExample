package tongtong.lapin.lint.exemple.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import tongtong.lapin.lint.exemple.R
import tongtong.lapin.lint.exemple.examples.Example6

class AnotherActivity : AppCompatActivity() {
    private val example5 = Example6.create(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_another)

        example5.addTag("TestTag1")
        example5.run(this)
    }
}