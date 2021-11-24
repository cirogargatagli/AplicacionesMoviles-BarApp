package com.example.barapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class ForgotPassActivity : AppCompatActivity() {

    private lateinit var  txtEmail: TextInputEditText
    private lateinit var auth:FirebaseAuth
    private lateinit var  progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pass)

        txtEmail = findViewById(R.id.txtEmailForgot_edit_text)
        auth = FirebaseAuth.getInstance()
        progressBar = findViewById(R.id.progressBarForgot)
    }

    fun send(view:View){
        val email = txtEmail.text.toString()

        if(!TextUtils.isEmpty(email)){
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this){
                    task ->

                    if(task.isSuccessful){
                        Toast.makeText(this, R.string.password_sending, Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }else{
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, R.string.error_send_mail, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}