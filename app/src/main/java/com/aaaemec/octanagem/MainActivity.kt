package com.aaaemec.octanagem

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.aaaemec.octanagem.Fragments.*
import com.aaaemec.octanagem.Model.Produtos
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val mAuth = FirebaseAuth.getInstance()
    val mFirebaseUser = mAuth.getCurrentUser()
    val mDB = FirebaseFirestore.getInstance()
    val ref = mDB.collection("Users")

    companion object {
        /*
         * Para debug em LogCat durante o desenvolvimento do
         * projeto.
         * */
        const val LOG = "log-bs"

        const val FRAGMENT_TAG = "frag-tag"
        const val FRAGMENT_ID = "frag-id"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val options = FirebaseApp.getInstance().options
        if (FirebaseApp.getApps(this).isEmpty()) {
            val anotherApp = FirebaseApp.initializeApp(this, options, "Octanagem")
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        val toggle: ActionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        val naview: NavigationView = findViewById(R.id.nav_view)
        naview.setNavigationItemSelectedListener(this)

        updateHeader()
        updatesocio()
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
        }


    }

    fun callLoginActivity(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    fun updateToolbarTitleInFragment(titleStringId: Int) {
        toolbar.title = getString(titleStringId)
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
        } else if (id == R.id.nav_loja) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, LojaFragment()).commit()
            Toast.makeText(this, "Galeria", Toast.LENGTH_SHORT).show()
        } else if (id == R.id.nav_carteira) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, CardFragment()).commit()
            Toast.makeText(this, "Slide", Toast.LENGTH_SHORT).show()
        } else if (id == R.id.socio) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, SocioFragment()).commit()
            Toast.makeText(this, "Socio", Toast.LENGTH_SHORT).show()
        } else if (id == R.id.nav_carrinho) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, CartFragment()).commit()
            Toast.makeText(this, "Carrinho", Toast.LENGTH_SHORT).show()
        } else if (id == R.id.nav_parceiros) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, ParceiroFragment()).commit()
            Toast.makeText(this, "Parceiros", Toast.LENGTH_SHORT).show()
        }else if (id == R.id.nav_logout) {
            mAuth.signOut()
            finish()
            val intToHome = Intent(this, LoginActivity::class.java)
            startActivity(intToHome)
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()
        }

        return true
    }

    private fun initFragment() {
        val supFrag = supportFragmentManager
        var fragment = supFrag.findFragmentByTag(FRAGMENT_TAG)

        /*
         * Se não for uma reconstrução de atividade, então não
         * haverá um fragmento em memória, então busca-se o
         * inicial.
         * */
        if (fragment == null) {

            /*
             * Caso haja algum ID de fragmento em intent, então
             * é este fragmento que deve ser acionado. Caso
             * contrário, abra o fragmento comum de início.
             * */
            var fragId = intent?.getIntExtra(FRAGMENT_ID, 0)
            if (fragId == 0) {
                fragId = R.id.nav_home
            }

            fragment = getFragment(fragId!!.toLong())
        }

        replaceFragment(fragment)
    }

    private fun getFragment(fragId: Long): Fragment {

        return when (fragId) {
            R.id.nav_loja.toLong() -> LojaFragment()
            R.id.nav_carteira.toLong() -> CardFragment()
            else -> HomeFragment()
        }
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragment_container,
                fragment,
                FRAGMENT_TAG
            )
            .commit()
    }


    fun updateHeader() {

        mFirebaseUser?.let {
            val uid = mFirebaseUser.uid
            ref.document(uid).get()
                .addOnSuccessListener { document ->
                    val nome = document.getString("username")
                    val info = document.getString("email")
                    val img = document.getString("profileImageUrl")

                        if (document != null) {
                            tv_nome.text = nome
                            tv_info.text = info
                            if (!img!!.isEmpty()) {
                                Picasso.get().load(img).into(iv_nav)
                            }
                        }

                    }
                }






    }

    fun updatesocio() {
        mFirebaseUser?.let {
            val uid = mFirebaseUser.uid
            ref.document(uid).collection("Status").document("socio").get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val Socio = document.getString("status")

                        Log.d("Socio", "$Socio")
                        if (Socio != "pendente") {
                            naview()
                        }
                    }
                    }

                }



    }

    fun naview(){
        val naview: NavigationView = findViewById(R.id.nav_view)
        val menu : Menu = naview.menu

        menu.findItem(R.id.socio).setVisible(false)
        menu.findItem(R.id.nav_carteira).setVisible(true)
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

class Status(val status: String?) {
    constructor() : this("")

}
class Id(val id: Int){
constructor() : this(0)
}

