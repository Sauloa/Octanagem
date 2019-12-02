package com.aaaemec.octanagem.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aaaemec.octanagem.Model.Produtos
import com.aaaemec.octanagem.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.FirebaseDatabase
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aaaemec.octanagem.Adapter.ProductAdapter
import com.google.firebase.firestore.FirebaseFirestore
import java.nio.file.Paths.get


class LojaFragment : Fragment() {

    lateinit var list: ArrayList<Produtos>
    var db = FirebaseFirestore.getInstance()

    lateinit var RecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = LayoutInflater
            .from(container?.context)
            .inflate(
                R.layout.loja_fragment,
                container,
                false
            )


        list = arrayListOf()
        RecyclerView = v.findViewById(R.id.recyclerview)
        RecyclerView.setHasFixedSize(true)
        RecyclerView.layoutManager = LinearLayoutManager(context)
        db.collection("Produtos").document("sqVfS0Ua72W1P83SP6LP").get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val index = document.getLong("index")
                    for(x in 1 until index!!+1 step 1){
                        val title = document.getString("title_$x")
                        val price = document.getString("price_$x")
                        val desc = document.getString("desc_$x")
                        val img = document.getString("img_$x")
                        val valor = document.getString("valor_$x")

                        val m = Produtos(title!!,price!!,img!!,desc!!,valor!!)
                        list.add(m)
                        Log.d("Tag", "existe: $valor")
                    }
                    val adapter = ProductAdapter(activity!!,list )
                    RecyclerView.adapter = adapter
                }

            }

        return v
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
