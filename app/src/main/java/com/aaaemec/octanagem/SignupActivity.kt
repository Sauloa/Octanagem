package com.aaaemec.octanagem

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.net.Uri
import android.graphics.drawable.BitmapDrawable
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.blankj.utilcode.util.ActivityUtils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.content_signup.*
import java.util.*

class SignupActivity : AppCompatActivity() {

    companion object {
        val TAG = "RegisterActivity"
    }

    val mAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)


        btn_signUp.setOnClickListener {
            val email = et_email_signup.text.toString()
            val password = et_pass_signup.text.toString()
            val repassword = et_repass_signup.text.toString()
            val user = et_user.text.toString()

            Log.d("Login", "Login com email e senh: $email")


            if (email.isEmpty()) {
                et_email_signup.setError("Entre com um Email")
                et_email_signup.requestFocus()
            } else if (password.isEmpty()) {
                et_pass_signup.setError("Entre com uma Senha")
                et_pass_signup.requestFocus()
            } else if (password != repassword) {
                Toast.makeText(this, "Senhas diferentes", Toast.LENGTH_SHORT).show()
            } else if (email.isEmpty() && password.isEmpty()) {
                Toast.makeText(this, "Todos os campos estâo vazios!", Toast.LENGTH_SHORT).show()
            } else if (!(email.isEmpty() && password.isEmpty())) {
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this,
                        OnCompleteListener<AuthResult> { task ->
                            if (!task.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "Erro, tente novamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                uploadImageToFirebaseStorage()

                                val intToHome = Intent(this, SplashActivity::class.java)
                                startActivity(intToHome)
                                finish()
                            }
                        })
            } else {
                Toast.makeText(this, "Error Occurred!", Toast.LENGTH_SHORT).show()

            }

        }

        bt_login2.setOnClickListener {
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
        }
        selectphoto_button_register.setOnClickListener {
            Log.d(TAG, "Try to show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }


    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was....
            Log.d(TAG, "Photo was selected")
            naview()
            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectphoto_imageview_register.setImageBitmap(bitmap)

            selectphoto_button_register.alpha = 0f

            //val bitmapDrawable = BitmapDrawable(bitmap)
            // selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)
        }
    }

    fun naview() {
        val botton: Button = findViewById(R.id.selectphoto_button_register)
        val image: ImageView = findViewById(R.id.selectphoto_imageview_register)

        botton.visibility = View.GONE
        image.visibility = View.VISIBLE
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG, "File Location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to upload image to storage: ${it.message}")
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user =
            User(uid, et_user.text.toString(), profileImageUrl, et_email_signup.text.toString())

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "Finally we saved the user to Firebase Database")
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to set value to database: ${it.message}")
            }
        ref.child("status").setValue("pendente")
        ref.child("id").setValue(0)
    }


}

class User(val uid: String, val username: String, val profileImageUrl: String, val email: String) {
    constructor() : this("", "", "", "")
}