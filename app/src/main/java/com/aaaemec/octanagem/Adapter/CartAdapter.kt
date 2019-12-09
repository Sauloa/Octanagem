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
import com.aaaemec.octanagem.Fragments.CartFragment
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso

class CartAdapter(var ctx: FragmentActivity, var items: ArrayList<Cart>) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(ctx).inflate(R.layout.cart_items, parent, false)
        val vholder = ViewHolder(view)






        return vholder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], ctx)

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.cart_titulo)
        val price: TextView = view.findViewById(R.id.cart_price)
        val thumbnail: ImageView = view.findViewById(R.id.cart_img)
        val btn : Button = view.findViewById(R.id.btn_cartitem)
        var id : Long = 0
        val mDB = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref: Query = mDB.collection("Carrinhos").document(uid).collection("Produto")
        val del = mDB.collection("Carrinhos").document(uid).collection("Produto")
        val cart: CartFragment = CartFragment()


        fun bind(cart: Cart, ctx: FragmentActivity) {

            title.text = cart.title
            id = cart.id!!
            price.text = cart.price
            if (cart.thumbnail != null) {
                Picasso.get().load(cart.thumbnail).into(thumbnail)
            } else {

            }


            btn.setOnClickListener(object : View.OnClickListener{
                override fun onClick(v: View?) {
                    val ID = ref.whereEqualTo("id",id)
                    del.document(id.toString()).delete()
                        .addOnCompleteListener { Log.d("Tag","Item Removido") }
                    Log.d("Tag","Id: "+ID.toString())
                    Log.d("Tag","Id1: "+id)

                    ctx.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, CartFragment()).commit()



                }


            } )


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

}
