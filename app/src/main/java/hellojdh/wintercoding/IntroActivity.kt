package hellojdh.wintercoding

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import hellojdh.wintercoding.tool.BaseActivity

class IntroActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        goMain()
    }

    private fun goMain(){
        val intent = Intent(this,MainActivity::class.java)
        Handler().postDelayed({
            startActivity(intent)
            finish()
        },INTRO_TIME)
    }

    companion object {
        const val INTRO_TIME : Long = 1300
    }
}