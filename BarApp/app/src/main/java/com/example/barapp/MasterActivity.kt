package com.example.barapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.barapp.databinding.ActivityMasterBinding
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import java.io.File
import java.util.*

class MasterActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMasterBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var  dbReference: DatabaseReference
    private lateinit var storageReference : StorageReference

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

        navView.menu.findItem(R.id.nav_settings).setOnMenuItemClickListener {
            goSettings()
            true
        }

        val header = navView.getHeaderView(0)
        val prefs = getSharedPreferences("Usuario", Context.MODE_PRIVATE)
        val headerName = header.findViewById<TextView>(R.id.txtNameNav)

        val headerImg = header.findViewById<ImageView>(R.id.imgHeader)
        val imgPerfil = prefs.getString("Imagen", "").toString()

        headerImg.setOnClickListener(){
            val intent = Intent(CropImage.getPickImageChooserIntent(this))
            resultLauncher.launch(intent)
        }

        if(imgPerfil != ""){
            Picasso.get()
                .load(imgPerfil)
                .into(headerImg)
        }

        headerName.text = prefs.getString("Nombre", R.string.welcome.toString()) + " " + prefs.getString("Apellido", "")
        header.findViewById<TextView>(R.id.txtEmailNav).text = auth.currentUser?.email.toString()

        val navController = findNavController(R.id.nav_host_fragment_content_master)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_reservation
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri =  CropImage.getPickImageResultUri(this,result.data)

            val loadingDialog = ProgressDialog(this)
            loadingDialog.setTitle(R.string.upload_perfil)
            loadingDialog.progress = 10
            loadingDialog.max = 100
            loadingDialog.setMessage(getString(R.string.upload))
            loadingDialog.show()

            val file = File(this.filesDir, auth.currentUser!!.uid)
            val bytes = imageUri?.let { this.contentResolver.openInputStream(it)?.readBytes() }!!
            file.writeBytes(bytes)
            dbReference = FirebaseDatabase.getInstance().reference.child("Usuario").child(auth.currentUser?.uid.toString()).child("Imagen")
            storageReference = FirebaseStorage.getInstance().reference.child(auth.currentUser?.uid.toString())

            storageReference.putFile(imageUri).addOnSuccessListener {
                if(it.task.isComplete){
                    val result = it.metadata?.reference?.downloadUrl
                    result?.addOnSuccessListener {
                        val downloadUri = it.toString()
                        dbReference.setValue(downloadUri)
                        val prefs = getSharedPreferences("Usuario", Context.MODE_PRIVATE).edit()
                        prefs.putString("Imagen", downloadUri)
                        prefs.apply()

                        val header = binding.navView.getHeaderView(0)
                        val headerImg = header.findViewById<ImageView>(R.id.imgHeader)
                        Picasso.get()
                            .load(downloadUri)
                            .into(headerImg)
                        loadingDialog.dismiss()
                        Toast.makeText(this,R.string.upload_succesfully, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun goSettings(){
        startActivity(Intent(this,SettingsActivity::class.java))
    }

    private fun action(){
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_master)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}