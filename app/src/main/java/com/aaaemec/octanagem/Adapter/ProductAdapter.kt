package com.aaaemec.octanagem.Adapter

import android.app.Dialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.aaaemec.octanagem.MainActivity
import com.aaaemec.octanagem.MainActivity.Companion.REQUEST_CODE
import com.aaaemec.octanagem.Model.Cart
import com.aaaemec.octanagem.Model.Produtos
import com.aaaemec.octanagem.R
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.mercadopago.android.px.core.MercadoPagoCheckout
import org.json.JSONObject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ProductAdapter(var ctx: FragmentActivity, var items: ArrayList<Produtos>) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    lateinit var dialog: Dialog
    val mDB = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().uid ?: ""
    val ref = mDB.collection("Carrinhos").document(uid)
    val id = mDB.collection("Carrinhos").document("id")
    val db = ref.collection("Id").document("id")
    var x: Long? = 0
    val token = "TEST-e287ed41-dee0-4d74-9039-dd68cf6f685e"
    val access = "TEST-8287893393395202-120104-07fb1f3b9d80e125fc9079d68475fdb1-240461056"


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(ctx).inflate(R.layout.cardview, parent, false)
        val vholder = ViewHolder(view)


        //db.set(Id)





        dialog = Dialog(ctx)
        dialog.setContentView(R.layout.produto_fragment)

        Log.d("Tag", "X é: $x")
        vholder.card.setOnClickListener {
            val titleP: TextView = dialog.findViewById(R.id.produtos_titulo)
            val priceP: TextView = dialog.findViewById(R.id.produtos_price)
            val descP: TextView = dialog.findViewById(R.id.produtos_desc)
            val valorP: TextView = dialog.findViewById(R.id.produtos_valor)
            val thumbnailP: ImageView = dialog.findViewById(R.id.imageView_Produto)
            val cart: TextView = dialog.findViewById(R.id.tv_addcart)
            val btn: Button = dialog.findViewById(R.id.btn_buynow)



            titleP.text = items[vholder.adapterPosition].title
            priceP.text = addMask(items[vholder.adapterPosition].price,"R$###")
            descP.text = items[vholder.adapterPosition].desc
            valorP.text = items[vholder.adapterPosition].valor
            if (items.get(vholder.adapterPosition).thumbnail != null) {
                Picasso.get().load(items[vholder.adapterPosition].thumbnail).into(thumbnailP)
            } else {

            }
            db.get()
                .addOnSuccessListener { document ->
                    x = document.getLong("id")
                }
            cart.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {

                    val title = titleP.text.toString()
                    val price = priceP.text.toString()
                    val thumbnail = items[vholder.adapterPosition].thumbnail
                    val valor = valorP.text.toString()
                    val produto = Cart(title, price, thumbnail, x, valor)

                    ref.collection("Produto").document(x.toString()).set(produto)
//                    ref.collection("Id").document("id").set(Id)
                    db.update("id", FieldValue.increment(1))

                    Toast.makeText(
                        ctx,
                        "Produto adicionado ao carrinho. " + x,
                        Toast.LENGTH_LONG
                    ).show()
                    dialog.dismiss()
//                    ctx.supportFragmentManager.beginTransaction()
//                        .replace(R.id.fragment_container, LojaFragment()).commit()
                }
            })

            btn.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    val title = titleP.text.toString()
                    val valor = valorP.text.toString()

                    val url = "https://api.mercadopago.com/checkout/preferences?access_token=TEST-8287893393395202-120104-07fb1f3b9d80e125fc9079d68475fdb1-240461056"


                    val queue = Volley.newRequestQueue(ctx)

                    val strJson = "{\n" +
                            "           \"items\": [\n" +
                            "               {\n" +
                            "               \"title\": \"$title\",\n" +
                            "               \"description\": \"Multicolor Item\",\n" +
                            "               \"quantity\": 1,\n" +
                            "               \"currency_id\": \"BRL\",\n" +
                            "               \"unit_price\": $valor\n" +
                            "               }\n" +
                            "           ],\n" +
                            "           \"payer\": {\n" +
                            "               \"email\": \"payer@email.com\"\n" +
                            "           }\n" +
                            "     }"

                    val obj = JSONObject(strJson)

                    //val url = "https://api.mercadopago.com/checkout/preferences"
                    val stringRequest = object : JsonObjectRequest(Method.POST, url, obj,
                        Response.Listener<JSONObject> {response ->
                            val checkoutPreferenceId: String= response.getString("id")
                            MercadoPagoCheckout.Builder(token, checkoutPreferenceId)
                                .build().startPayment(ctx, REQUEST_CODE)
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

                    Toast.makeText(
                        ctx,
                        "Clicou",
                        Toast.LENGTH_LONG
                    ).show()

                }



            })

            dialog.show()

            Toast.makeText(
                ctx,
                "Clicou em: " + vholder.adapterPosition.toString(),
                Toast.LENGTH_LONG
            ).show()
        }


        return vholder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], ctx)

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.produto_titulo)
        val price: TextView = view.findViewById(R.id.produto_price)
        val desc: TextView = view.findViewById(R.id.produto_desc)
        val valor: TextView = view.findViewById(R.id.produto_valor)
        val thumbnail: ImageView = view.findViewById(R.id.produto_img)
        val card: CardView = view.findViewById(R.id.cardview_id)


        fun bind(produtos: Produtos, ctx: FragmentActivity) {

            title.text = produtos.title
            desc.text = produtos.desc
            valor.text = produtos.valor
            price.text = addMask(produtos.price, "R$####")
            if (produtos.thumbnail != null) {
                Picasso.get().load(produtos.thumbnail).into(thumbnail)
            } else {
                thumbnail.setImageResource(R.drawable.ic_menu_camera)
            }
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

    fun pagamento(preference: String) {
        MercadoPagoCheckout.Builder(token, preference).build()
            .startPayment(ctx, MainActivity.REQUEST_CODE)

    }

}

class id(val id: Long) {
    constructor() : this(0)
}