package com.aaaemec.octanagem.Fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.aaaemec.octanagem.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.socio_fragment.*

class SocioFragment : Fragment() {
    val mAuth = FirebaseAuth.getInstance()
    val mFirebaseUser = mAuth.getCurrentUser()
    val mDB = FirebaseDatabase.getInstance()
    val ref = mDB.getReference().child("users")
    val uid = FirebaseAuth.getInstance().uid ?: ""

    val nome = R.id.prof_nome.toString()
    val nacionalidade = R.id.prof_nacionalidade.toString()
    val natural = R.id.prof_natural.toString()
    val data = R.id.prof_nascimento.toString()
    val civil = R.id.prof_civil.toString()
    val cpf = R.id.prof_cpf.toString()
    val rg = R.id.prof_rg.toString()
    val ende = R.id.prof_ende.toString()
    val num = R.id.prof_num.toString()
    val cidade = R.id.prof_cidade.toString()
    val estado = R.id.prof_estado.toString()
    val bairro = R.id.prof_bairro.toString()
    val cep = R.id.prof_cep.toString()
    val tel = R.id.prof_tel.toString()
    val mat = R.id.prof_mat.toString()
    val ies = R.id.prof_ies.toString()
    val curso = R.id.prof_curso.toString()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater.inflate(R.layout.socio_fragment, container, false)

        val bt: Button = view.findViewById(R.id.btn_prof)
        bt.setOnClickListener(View.OnClickListener {
            OnClick(view)
        })

        return view
    }

    fun OnClick(v: View) {

        if (nome.isEmpty() || nacionalidade.isEmpty() || data.isEmpty() || civil.isEmpty() || cpf.isEmpty()
            || rg.isEmpty() || ende.isEmpty() || natural.isEmpty() || num.isEmpty() || cidade.isEmpty() ||
            bairro.isEmpty() || estado.isEmpty() || cep.isEmpty() || tel.isEmpty() || mat.isEmpty()
        ) {
            Toast.makeText(context, "Todos os campos são obrigatorios", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Tudo certo", Toast.LENGTH_SHORT).show()
            saveUserInfo(
                uid, nome, nacionalidade, natural, data, civil, cpf, rg, ende,
                num, bairro, cidade, estado, cep,
                tel, mat, ies, curso
            )
            replaceFragment(HomeFragment())
        }


    }

    fun replaceFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragment_container,
                fragment,
                MainActivity.FRAGMENT_TAG
            )
            .commit()
    }

    fun saveUserInfo(
        uid: String, nome: String, nacionalidade: String, natural: String,
        data: String, civil: String, cpf: String, rg: String, ende: String,
        num: String, bairro: String, cidade: String, estado: String, cep: String,
        tel: String, matricula: String, ies: String, curso: String
    ) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val nome = prof_nome.text.toString()
        val nacionalidade = prof_nacionalidade.text.toString()
        val natural = prof_natural.text.toString()
        val nascimento = prof_nascimento.text.toString()
        val civil = prof_civil.text.toString()
        val cpf = prof_cpf.text.toString()
        val rg = prof_rg.text.toString()
        val ende = prof_ende.text.toString()
        val num = prof_num.text.toString()
        val cidade = prof_cidade.text.toString()
        val estado = prof_estado.text.toString()
        val bairro = prof_bairro.text.toString()
        val cep = prof_cep.text.toString()
        val tel = prof_tel.text.toString()
        val mat = prof_mat.text.toString()
        val ies = prof_ies.text.toString()
        val curso = prof_curso.text.toString()

        val user = Profile(
            uid, nome, nacionalidade, natural, nascimento, civil, cpf, rg, ende, num, bairro, cidade,
            estado, cep, tel, mat, ies, curso
        )

        ref.child("socio").setValue(user)
        Log.d("Socio","$user")
    }
}

class Profile(
    val uid: String?,
    val nome: String?,
    val nacionalidade: String?,
    val natural: String?,
    val data: String?,
    val civil: String?,
    val cpf: String?,
    val rg: String?,
    val ende: String?,
    val num: String?,
    val bairro: String?,
    val cidade: String?,
    val estado: String?,
    val cep: String?,
    val tel: String?,
    val matricula: String?,
    val ies: String?,
    val curso: String?
) {
    constructor() : this(
        "", "", "", "", "", "", "", "", "", "",
        "", "", "", "", "", "", "", ""
    )
}

