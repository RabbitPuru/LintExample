package tongtong.lapin.lint.exemple.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import tongtong.lapin.lint.exemple.R
import tongtong.lapin.lint.exemple.examples.Example6

class MainActivity : AppCompatActivity() {
    private val example6 = Example6.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        example6.run(this)
        example6.run(listOf(this, applicationContext))
    }
}
