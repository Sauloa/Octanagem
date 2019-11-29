package com.aaaemec.octanagem

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : Activity() {

    val mAuth = FirebaseAuth.getInstance()
    val mFirebaseUser = mAuth.getCurrentUser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        Handler().postDelayed({
                splash()
        }, 3000)
    }

    fun splash() {
        if (mFirebaseUser != null) {
            val i = Intent()
            i.setClass(this, MainActivity::class.java)
            startActivity(i)
            finish()
        } else {
            val i = Intent()
            i.setClass(this, LoginActivity::class.java)
            startActivity(i)
            finish()
        }
    }
}
