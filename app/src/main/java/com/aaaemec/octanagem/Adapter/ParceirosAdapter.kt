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
import com.aaaemec.octanagem.Model.Parceiros
import com.aaaemec.octanagem.Model.Produtos
import com.aaaemec.octanagem.R
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.mercadopago.android.px.core.MercadoPagoCheckout
import org.json.JSONObject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ParceirosAdapter(var ctx: FragmentActivity, var items: ArrayList<Parceiros>) :
    RecyclerView.Adapter<ParceirosAdapter.ViewHolder>(), OnMapReadyCallback {

    lateinit var dialog: Dialog
    lateinit var mMap: GoogleMap
    val mDB = FirebaseFirestore.getInstance()
    val ref = mDB.collection("Parceiria")
    var x: Long? = 0
    var latitude = 0.0
    var longitude = 0.0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(ctx).inflate(R.layout.parceiro_items, parent, false)
        val vholder = ViewHolder(view)


        //db.set(Id)




        dialog = Dialog(ctx)
        dialog.setContentView(R.layout.activity_maps)

        Log.d("Tag", "X é: $x")
        vholder.card.setOnClickListener {
            latitude = items[vholder.adapterPosition].lat
            longitude = items[vholder.adapterPosition].long

            val mapFragment = ctx.supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)


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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(latitude, longitude)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12.0f))
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.parceiro_titulo)
        val time: TextView = view.findViewById(R.id.parceiro_time)
        val thumbnail: ImageView = view.findViewById(R.id.img_parc)
        val card: CardView = view.findViewById(R.id.card_parceiro)
        var lat: Double = 0.0
        var long: Double = 0.0


        fun bind(produtos: Parceiros, ctx: FragmentActivity) {

            title.text = produtos.title
            time.text = produtos.time
            lat = produtos.lat
            long = produtos.long

            if (produtos.thumbnail != null) {
                Picasso.get().load(produtos.thumbnail).into(thumbnail)
            } else {
                thumbnail.setImageResource(R.drawable.ic_menu_camera)
            }
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