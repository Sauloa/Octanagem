package com.aaaemec.octanagem

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import kotlinx.android.synthetic.main.activity_login.*
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ScreenUtils
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.android.synthetic.main.content_login.*


class LoginActivity : AppCompatActivity() {



    val mAuth = FirebaseAuth.getInstance()
    val mFirebaseUser = mAuth.getCurrentUser()
    val mAuthStateListener = FirebaseAuth.AuthStateListener {

        if (mFirebaseUser != null) {
            Toast.makeText(this, "VocÃª estÃ¡ logado", Toast.LENGTH_SHORT).show()
            val i = Intent(this@LoginActivity, MainActivity::class.java)
            ActivityUtils.startActivity(i)
        } else {
            Toast.makeText(this@LoginActivity, "FaÃ§a o login", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)

        bt_login.setOnClickListener {
            val email = et_email.text.toString()
            val password = et_password.text.toString()

            Log.d("Login", "Login com email e senh: $email")


            if (email.isEmpty()) {
                et_email.setError("Entre com um Email")
                et_email.requestFocus()
            } else if (password.isEmpty()) {
                et_password.setError("Entre com uma Senha")
                et_password.requestFocus()
            } else if (email.isEmpty() && password.isEmpty()) {
                Toast.makeText(this, "Todos os campos estÃ£o vazios!", Toast.LENGTH_SHORT).show()
            } else if (!(email.isEmpty() && password.isEmpty())) {
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this,
                        OnCompleteListener<AuthResult> { task ->
                            if (!task.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "Login Error, Please Login Again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val intToHome = Intent(this, MainActivity::class.java)
                                startActivity(intToHome)
                                finish()
                            }
                        })
            } else {
                Toast.makeText(this, "Error Occurred!", Toast.LENGTH_SHORT).show()

            }

        }


        tv_signup.setOnClickListener {
            val i = Intent(this, SignupActivity::class.java )
            startActivity(i)
        }


    }




}
