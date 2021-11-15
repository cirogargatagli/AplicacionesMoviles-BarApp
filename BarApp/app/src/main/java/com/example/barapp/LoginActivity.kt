package com.example.barapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        txtMail = findViewById(R.id.txtLoginEmail)
        txtPassword = findViewById(R.id.txtLoginPass)
        progressBar = findViewById(R.id.progressBarLogin)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        dbReference = database.reference.child("Usuario")
        if(auth.currentUser != null){
            action()
        }
    }

    fun login(view: View){
        loginUser()
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
                                    val rol = it.child("Rol").value
                                    if(rol == "User"){
                                        action()
                                    }
                                }
                        }else{
                            progressBar.visibility = View.GONE
                            Toast.makeText(this, "Error al autenticarse.", Toast.LENGTH_LONG).show()
                        }
            }

        }

    }

    private fun action(){
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
}