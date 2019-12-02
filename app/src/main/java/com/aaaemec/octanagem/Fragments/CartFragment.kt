package com.aaaemec.octanagem.Fragments

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aaaemec.octanagem.Adapter.CartAdapter
import com.aaaemec.octanagem.Adapter.ProductAdapter
import com.aaaemec.octanagem.MainActivity
import com.aaaemec.octanagem.Model.Cart
import com.aaaemec.octanagem.Model.Produtos
import com.aaaemec.octanagem.R
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.mercadopago.android.px.core.MercadoPagoCheckout
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
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
        val btn: Button = v.findViewById(R.id.btn_finish)
        val itemJsonArray: JSONArray = JSONArray()
        val token = "TEST-e287ed41-dee0-4d74-9039-dd68cf6f685e"
        val dbvazia = db.collection("Carrinhos").document(uid).collection("Produto")

        list = arrayListOf()
        RecyclerView = v.findViewById(R.id.rv_cart)
        RecyclerView.setHasFixedSize(true)
        RecyclerView.layoutManager = LinearLayoutManager(context)

        if (dbvazia != null) {
            db.collection("Carrinhos").document(uid).collection("Produto").get()
                .addOnSuccessListener { document ->
                    var total = 0
                    var x = 0

                    for (document in document) {
                        val title = document.getString("title")
                        val price = document.getString("price")
                        val img = document.getString("thumbnail")
                        val id = document.getLong("id")
                        val valor = document.getString("valor")

                        val m = Cart(title!!, price!!, img!!, id!!, valor!!)
                        list.add(m)


                        val valorl = Integer.valueOf(valor)
                        val itemJSON: JSONObject = JSONObject()


                        itemJSON.put("title", title)
                        itemJSON.put("quantity", 1)
                        itemJSON.put("currency_id", "BRL")
                        itemJSON.put("unit_price", valorl)

                        itemJsonArray.put(itemJSON)

                        val sum = Integer.valueOf(valor)
                        total += sum
                        Log.d("Tag", "existe: $title")
                        Log.d("Tag", "items: $itemJSON")
                        Log.d("Tag", "items na Array: $itemJsonArray")
                        Log.d("Tag", "items na list: $list")

                    }

                    value.text = addMask(total.toString(), "R$#####")
                    val adapter = CartAdapter(activity!!, list)
                    RecyclerView.adapter = adapter
                    Log.d("Tag", "Tamanho da lista: ${list.size}")

                }
        } else {
            value.text = addMask("00", "R$##")

        }

        btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {


                try {
                    for (i in 0 until list.size) {


                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }


//                val dados = "               {\n" +
//                        "               \"title\": \"$title\",\n" +
//                        "               \"description\": \"Multicolor Item\",\n" +
//                        "               \"quantity\": 1,\n" +
//                        "               \"currency_id\": \"BRL\",\n" +
//                        "               \"unit_price\": $valor\n" +
//                        "               },\n" +""


                val url =
                    "https://api.mercadopago.com/checkout/preferences?access_token=TEST-8287893393395202-120104-07fb1f3b9d80e125fc9079d68475fdb1-240461056"


                val queue = Volley.newRequestQueue(activity)

                val strJson = "{\n" +
                        "           \"items\":\n" +
                        itemJsonArray +
                        "           ,\n" +
                        "           \"payer\": {\n" +
                        "               \"email\": \"payer@email.com\"\n" +
                        "           }\n" +
                        "     }"

                val obj = JSONObject(strJson)


                val stringRequest = object : JsonObjectRequest(
                    Method.POST, url, obj,
                    Response.Listener<JSONObject> { response ->
                        val checkoutPreferenceId: String= response.getString("id")
                        MercadoPagoCheckout.Builder(token, checkoutPreferenceId)
                            .build().startPayment(context!!, MainActivity.REQUEST_CODE)
                    },
                    Response.ErrorListener { error ->
                        val erros = error.toString()

                    }

                ) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-Type"] = "application/json"
                        return headers
                    }

                }

                val policy = DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                stringRequest.retryPolicy = policy
                queue.add(stringRequest)

                Log.d("TAG", strJson)

            }


        })



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


