package com.aaaemec.octanagem.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.aaaemec.octanagem.R
import com.aaaemec.octanagem.User
import com.aaaemec.octanagem.Fragments.Profile
import com.aaaemec.octanagem.MainActivity
import com.aaaemec.octanagem.Status
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.card_fragment.*
import kotlinx.android.synthetic.main.socio_fragment.*

class CardFragment : Fragment() {

    val mAuth = FirebaseAuth.getInstance()
    val mFirebaseUser = mAuth.getCurrentUser()
    val mDB = FirebaseFirestore.getInstance()
    val ref = mDB.collection("Users")
    val uid = FirebaseAuth.getInstance().uid ?: ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater
            .inflate(
                R.layout.card_fragment,
                container,
                false
            )
        updatecard()
        updatephoto()

        return view
    }

    fun updatecard() {

        mFirebaseUser?.let {
            val uid = mFirebaseUser.uid
            ref.document(uid).collection("socio").document("dados").get()
                .addOnSuccessListener { document ->
                if (document != null) {
                            tv_name.text = document.getString("nome")
                            tv_ies.text = document.getString("ies")
                            tv_curso.text = document.getString("curso")
                            tv_cpf.text = addMask(document.getString("cpf"), "###.###.###-##")
                            tv_rg.text = addMask(document.getString("rg"),"##.###.###-##")
                            tv_nascimento.text = addMask(document.getString("data"),"##/##/####")
                            tv_mat.text = document.getString("matricula")
                            tv_id.text = addMask(document.getString("uid"),"###-###")


                        }

                    }
                }



            ref.document(uid).collection("Status").document("socio").get()
                .addOnSuccessListener { document ->
                    val nome = document.getString("status")

                    if (document != null) {
                        tv_status.text = nome
                    }

                }




    }
    fun addMask(text: String?, mask: String): String{
        var formatado: String = ""
        var i = 0
        for(m: Char in mask.toCharArray()){
            if(m != '#'){
                formatado += m
                continue
            }
            try{
                formatado += text!![i]
            } catch(e: Exception){
                break
            }
            i++
        }
        return formatado
    }

    fun updatephoto() {

        mFirebaseUser?.let {
            val uid = mFirebaseUser.uid
            ref.document(uid).get()
                .addOnSuccessListener { document ->
                    val img = document.getString("profileImageUrl")

                    if (document != null) {

                        if (!img!!.isEmpty()) {
                            Picasso.get().load(img).into(img_card)
                        }
                    }

                }
        }


    }
}
