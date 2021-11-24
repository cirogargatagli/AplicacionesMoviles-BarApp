package com.example.barapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var  txtName: TextInputEditText
    private lateinit var  txtLastName:TextInputEditText
    private lateinit var  txtEmail:TextInputEditText
    private lateinit var  txtPassword:TextInputEditText
    private lateinit var  progressBar:ProgressBar
    private lateinit var  dbReference: DatabaseReference
    private lateinit var  database:FirebaseDatabase
    private lateinit var  auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        txtName = findViewById(R.id.txtName_edit_text)
        txtLastName = findViewById(R.id.txtLastName_edit_text)
        txtEmail = findViewById(R.id.txtEmail_edit_text)
        txtPassword = findViewById(R.id.txtPassword_edit_text)


        progressBar = findViewById(R.id.progressBarRegister)
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        dbReference = database.reference.child("Usuario")
    }

    fun register(view:View){
        createNewAccount()
    }

    private fun createNewAccount(){
        val name:String=txtName.text.toString()
        val lastName:String=txtLastName.text.toString()
        val email:String=txtEmail.text.toString()
        val password:String=txtPassword.text.toString()

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(lastName) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            progressBar.visibility = View.VISIBLE

            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this) {
                    task ->
                    if(task.isComplete){
                        val user: FirebaseUser? = auth.currentUser
                        verifyEmail(user)
                        val userDB = dbReference.child(user?.uid.toString())

                        userDB.child("Nombre").setValue(name)
                        userDB.child("Apellido").setValue(lastName)
                        userDB.child("Email").setValue(email)
                        userDB.child("Rol").setValue("User")
                        action()
                    }
                }
        }
    }

    private fun action(){
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun verifyEmail(user:FirebaseUser?){
        user?.sendEmailVerification()
            ?.addOnCompleteListener(this){
                task ->
                if(task.isComplete){
                    Toast.makeText(this, R.string.send_mail, Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this, "Error al enviar el email", Toast.LENGTH_SHORT).show()
                }
            }
    }
}