package com.example.barapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.barapp.databinding.ActivityMasterBinding
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MasterActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMasterBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var  dbReference: DatabaseReference
    private lateinit var  database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMasterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMaster.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        auth = FirebaseAuth.getInstance()

        navView.menu.findItem(R.id.nav_logout).setOnMenuItemClickListener{
            val builder = AlertDialog.Builder(this)
            builder.setMessage(R.string.confirm_logout)
                .setCancelable(false)
                .setPositiveButton(R.string.logout) { dialog, id ->
                    val prefs = getSharedPreferences("Usuario", Context.MODE_PRIVATE).edit()
                    prefs.clear()
                    prefs.apply()
                    LoginManager.getInstance().logOut()
                    auth.signOut()
                    action()
                }
                .setNegativeButton(R.string.no) { dialog, id ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
            true
        }

        val header = navView.getHeaderView(0)
        val prefs = getSharedPreferences("Usuario", Context.MODE_PRIVATE)
        val headerName = header.findViewById<TextView>(R.id.txtNameNav)

        headerName.text = prefs.getString("Nombre", "Bienvenido") + " " + prefs.getString("Apellido", "")
        header.findViewById<TextView>(R.id.txtEmailNav).text = auth.currentUser?.email.toString()

        val navController = findNavController(R.id.nav_host_fragment_content_master)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun action(){
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.master, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_master)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}