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
import com.aaaemec.octanagem.Fragments.LojaFragment
import com.aaaemec.octanagem.Id
import com.aaaemec.octanagem.MainActivity
import com.aaaemec.octanagem.Model.Cart
import com.aaaemec.octanagem.Model.Produtos
import com.aaaemec.octanagem.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.util.*
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(ctx).inflate(R.layout.cardview, parent, false)
        val vholder = ViewHolder(view)
        val Id = id(0)

        mDB.collection("Carrinhos").document("id").set(Id)
        //db.set(Id)

        dialog = Dialog(ctx)
        dialog.setContentView(R.layout.produto_fragment)

        Log.d("Tag", "X é: $x")
        vholder.card.setOnClickListener {
            val titleP: TextView = dialog.findViewById(R.id.produtos_titulo)
            val priceP: TextView = dialog.findViewById(R.id.produtos_price)
            val descP: TextView = dialog.findViewById(R.id.produtos_desc)
            val thumbnailP: ImageView = dialog.findViewById(R.id.imageView_Produto)
            val cart: TextView = dialog.findViewById(R.id.tv_addcart)
            val btn: Button = dialog.findViewById(R.id.btn_buynow)


            titleP.text = items.get(vholder.adapterPosition).title
            priceP.text = items.get(vholder.adapterPosition).price
            descP.text = items.get(vholder.adapterPosition).desc
            if (items.get(vholder.adapterPosition).thumbnail != null) {
                Picasso.get().load(items.get(vholder.adapterPosition).thumbnail).into(thumbnailP)
            } else {

            }
            db.get()
                .addOnSuccessListener { document ->
                        x = document.getLong("id")
                        Log.d("Tag", "X1 é igual ${document.getLong("id")}")
                }
            cart.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {

                    val title = titleP.text.toString()
                    val price = addMask(priceP.text.toString(), "R$####")
                    val thumbnail = thumbnailP
                    val produto = Cart(title, price, thumbnail, x)
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
        val thumbnail: ImageView = view.findViewById(R.id.produto_img)
        val card: CardView = view.findViewById(R.id.cardview_id)


        fun bind(produtos: Produtos, ctx: FragmentActivity) {

            title.text = produtos.title
            desc.text = produtos.desc
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

}
class id(val id: Long){
    constructor(): this(0)
}