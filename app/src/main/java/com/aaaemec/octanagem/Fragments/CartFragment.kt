package com.aaaemec.octanagem.Fragments

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aaaemec.octanagem.Adapter.CartAdapter
import com.aaaemec.octanagem.Adapter.ProductAdapter
import com.aaaemec.octanagem.Model.Cart
import com.aaaemec.octanagem.Model.Produtos
import com.aaaemec.octanagem.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.math.BigDecimal
import java.math.RoundingMode.valueOf


@Suppress("NAME_SHADOWING")
class CartFragment : Fragment() {
    lateinit var tvTitle: TextView
    lateinit var tvPrice: TextView
    lateinit var iv: ImageView
    lateinit var list: ArrayList<Cart>
    val uid = FirebaseAuth.getInstance().uid ?: ""
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    lateinit var RecyclerView: RecyclerView
    lateinit var adapter: CartAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater
            .inflate(
                R.layout.cart_fragment,
                container,
                false
            )
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val value: TextView = v.findViewById(R.id.tv_value)
        val empty: LinearLayout = v.findViewById(R.id.ll_cart)
        val hand = Handler()
        val dbvazia = db.collection("Carrinho").document(uid).collection("Produto")

        list = arrayListOf()
        RecyclerView = v.findViewById(R.id.rv_cart)
        RecyclerView.setHasFixedSize(true)
        RecyclerView.layoutManager = LinearLayoutManager(context)

        if (dbvazia != null) {
            db.collection("Carrinho").document(uid).collection("Produto").get()
                .addOnSuccessListener { document ->
                    var total = 0
                    for (document in document) {
                        val title = document.getString("title")
                        val price = document.getString("price")
                        val img = document.getString("thumbnail")
                        val id = document.getLong("id")

                        val m = Cart(title!!, price!!, img!!, id!!)
                        list.add(m)
                        val sum = Integer.valueOf(price)
                        total += sum
                        Log.d("Tag", "existe: $title")

                    }
                    val adapter = CartAdapter(activity!!, list)
                    RecyclerView.adapter = adapter
                    Log.d("Tag", "RecyclerView Ativa")
                    Log.d("Tag", "RecyclerView Ativa3: ${list.size}")
                }
        } else {
            value.text = addMask("00", "R$##")
            empty.visibility = View.VISIBLE
        }



    return v
}

fun addMask(text: String, mask: String): String {
    var formatado: String = ""
    var i = 0
    for (m: Char in mask.toCharArray()) {
        if (m != '#') {
            formatado += m
            continue
        }
        try {
            formatado += text[i]
        } catch (e: Exception) {
            break
        }
        i++
    }
    return formatado
}

override fun onStop() {
    adapter = CartAdapter(activity!!, list)
    RecyclerView.adapter = adapter
    super.onStop()

}

override fun onDestroyView() {
    adapter = CartAdapter(activity!!, list)
    RecyclerView.adapter = adapter
    super.onDestroyView()

}
}


