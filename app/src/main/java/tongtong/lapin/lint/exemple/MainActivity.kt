package tongtong.lapin.lint.exemple

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val objet = CustomClass()
        objet.deprecatedFunction()
    }
}

class CustomClass {
    fun deprecatedFunction() {
        // did something... maybe
    }

    fun suggestedFunction() {
        // do something... maybe
    }
}