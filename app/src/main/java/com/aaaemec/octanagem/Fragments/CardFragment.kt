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
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.card_fragment.*
import kotlinx.android.synthetic.main.socio_fragment.*

class CardFragment : Fragment() {

    val mAuth = FirebaseAuth.getInstance()
    val mFirebaseUser = mAuth.getCurrentUser()
    val mDB = FirebaseDatabase.getInstance()
    val ref = mDB.getReference().child("users")
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
            ref.child(uid).child("socio").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val data = dataSnapshot.getValue(Profile::class.java)
                        Log.d("MainActivity", "")

                        if (data != null) {
                            tv_name.text = data.nome
                            tv_ies.text = data.ies
                            tv_curso.text = data.curso
                            tv_cpf.text = addMask(data.cpf, "###.###.###-##")
                            tv_rg.text = addMask(data.rg,"##.###.###-##")
                            tv_nascimento.text = addMask(data.data,"##/##/####")
                            tv_mat.text = data.matricula
                            tv_id.text = addMask(data.uid,"###-###")


                        }

                    }
                }

                override fun onCancelled(dataSnapshot: DatabaseError) {

                }


            })

            ref.child(uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val data = dataSnapshot.getValue(Status::class.java)
                    if (data != null) {
                        tv_status.text = data.status
                    }

                }

                override fun onCancelled(dataSnapshot: DatabaseError) {}


            })
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
            ref.child(uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val data = dataSnapshot.getValue(User::class.java)
                        Log.d("MainActivity", "")

                        if (data != null) {
                            if (data.profileImageUrl != null && !data.profileImageUrl.isEmpty()) {
                                Picasso.get().load(data.profileImageUrl).into(img_card)
                            }
                        }

                    }
                }

                override fun onCancelled(dataSnapshot: DatabaseError) {

                }


            })
        }


    }
}
