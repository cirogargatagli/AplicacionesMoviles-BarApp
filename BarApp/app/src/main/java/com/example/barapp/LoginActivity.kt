package com.example.barapp

import com.example.barapp.entity.Usuario
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var  txtMail: EditText
    private lateinit var  txtPassword: EditText
    private lateinit var  progressBar: ProgressBar
    private lateinit var  dbReference: DatabaseReference
    private lateinit var  database:FirebaseDatabase
    private lateinit var  auth: FirebaseAuth
    private lateinit var  btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        txtMail = findViewById(R.id.txtLoginEmail)
        txtPassword = findViewById(R.id.txtLoginPass)
        progressBar = findViewById(R.id.progressBarLogin)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        dbReference = database.reference.child("Usuario")
        btnLogin = findViewById(R.id.btnIngresar)

        btnLogin.setOnClickListener{
            loginUser()
        }
        if(auth.currentUser != null){
            action()
        }
    }

    fun forgotPassword(view: View){
        startActivity(Intent(this, ForgotPassActivity::class.java))
    }

    fun registerLogin(view: View){
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    private fun loginUser(){
        val mail:String=txtMail.text.toString()
        val pass:String=txtPassword.text.toString()

        if(!TextUtils.isEmpty(mail) && !TextUtils.isEmpty(pass)){
            progressBar.visibility = View.VISIBLE
            auth.signInWithEmailAndPassword(mail,pass)
                .addOnCompleteListener(this){
                        task ->
                        if(task.isSuccessful){
                            dbReference.child(auth.currentUser?.uid.toString())
                                .get().addOnSuccessListener {
                                    val hash = it.value as HashMap<*, *>
                                    var user = Usuario(hash["Nombre"].toString(),
                                        hash["Apellido"].toString(),
                                        hash["Email"].toString(),
                                        hash["Rol"].toString(),
                                        "")
                                    saveUserOnShared(user)


                                    if(user.rol == "User"){
                                        action()
                                    }
                                }
                        }else{
                            progressBar.visibility = View.GONE
                            Toast.makeText(this, R.string.error_login, Toast.LENGTH_LONG).show()
                        }
            }

        }

    }

    fun saveUserOnShared ( user: Usuario){
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = prefs.edit()
        editor.putString("Nombre", user.nombre)
        editor.putString("Apellido", user.apellido)
        editor.putString("Email", user.email)
        editor.putString("Rol", user.rol)
        editor.putString("Imagen", user.img)
        editor.apply()
    }

    private fun action(){
        val intent = Intent(this,MasterActivity::class.java)
        startActivity(intent)
        finish()
    }
}