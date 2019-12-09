package com.aaaemec.octanagem.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aaaemec.octanagem.Adapter.ParceirosAdapter
import com.aaaemec.octanagem.Model.Parceiros
import com.aaaemec.octanagem.R
import com.google.firebase.firestore.FirebaseFirestore

class ParceiroFragment : Fragment() {

    lateinit var list: ArrayList<Parceiros>
    var db = FirebaseFirestore.getInstance()

    lateinit var RecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = LayoutInflater
            .from(container?.context)
            .inflate(
                R.layout.parceiros_fragment,
                container,
                false
            )



        list = arrayListOf()
        RecyclerView = v.findViewById(R.id.rv_parceiros)
        RecyclerView.setHasFixedSize(true)
        RecyclerView.layoutManager = LinearLayoutManager(context)
        db.collection("Parceiros").get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    for(document in document){
                        val titulo = document.getString("titulo")
                        val time = document.getString("time")
                        val desc = document.getString("desc")
                        val img = document.getString("img")
                        val lat = document.getString("lat")!!.toDouble()
                        val long = document.getString("long")!!.toDouble()

                        val m = Parceiros(titulo!!,time!!,img!!,desc!!,lat,long)
                        list.add(m)
                    }
                    val adapter = ParceirosAdapter(activity!!,list )
                    RecyclerView.adapter = adapter
                }

            }

        return v
    }

    override fun onResume() {
        (activity as AppCompatActivity).supportActionBar?.hide()
        super.onResume()
    }

    override fun onStop() {
        (activity as AppCompatActivity).supportActionBar?.show()
        super.onStop()
    }

    fun addMask(text: String, mask: String): String{
        var formatado: String = ""
        var i = 0
        for(m: Char in mask.toCharArray()){
            if(m != '#'){
                formatado += m
                continue
            }
            try{
                formatado += text[i]
            } catch(e: Exception){
                break
            }
            i++
        }
        return formatado
    }

}


