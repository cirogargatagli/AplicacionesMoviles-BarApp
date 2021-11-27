package com.example.barapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import com.example.barapp.entity.Usuario
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.facebook.GraphRequest
import java.lang.Exception

class LoginActivity : AppCompatActivity() {

    private lateinit var  txtMail: TextInputEditText
    private lateinit var  txtPassword: TextInputEditText
    private lateinit var  progressBar: ProgressBar
    private lateinit var  dbReference: DatabaseReference
    private lateinit var  database:FirebaseDatabase
    private lateinit var  auth: FirebaseAuth
    private lateinit var  btnLogin: Button
    private lateinit var  btnFacebook: Button
    private lateinit var  btnGoogle: Button
    private lateinit var callbackManager: CallbackManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        txtMail = findViewById(R.id.txtLoginEmail_edit_text)
        txtPassword = findViewById(R.id.txtLoginPass_edit_text)
        progressBar = findViewById(R.id.progressBarLogin)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        dbReference = database.reference.child("Usuario")
        btnLogin = findViewById(R.id.btnIngresar)
        btnFacebook = findViewById(R.id.btnFacebook)
        btnGoogle = findViewById(R.id.btnGoogle)

        callbackManager = CallbackManager.Factory.create()

        btnLogin.setOnClickListener{
            loginUser()
        }

        btnGoogle.setOnClickListener{
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("958153719920-713b16hbltqu9tk5ct70d3vsi01pfpqu.apps.googleusercontent.com")
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()

            getResult.launch(googleClient.signInIntent)
        }

        btnFacebook.setOnClickListener{
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
            LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult>{
                override fun onSuccess(result: LoginResult?) {
                    result?.let {
                        val token = it.accessToken
                        val credential = FacebookAuthProvider.getCredential(token.token)
                        auth.signInWithCredential(credential)
                            .addOnCompleteListener(this@LoginActivity){
                                    task ->
                                        if(task.isSuccessful){
                                            dbReference.child(auth.currentUser?.uid.toString())
                                                .get().addOnSuccessListener {
                                                    if(it.value == null){
                                                        val request = GraphRequest.newMeRequest(
                                                            result.accessToken
                                                        ) { `object`, response ->
                                                            try {
                                                                val name = `object`["name"].toString()
                                                                val email = `object`["email"].toString()
                                                                val userDB = dbReference.child(auth.currentUser?.uid.toString())
                                                                userDB.child("Nombre").setValue(name)
                                                                userDB.child("Email").setValue(email)
                                                                userDB.child("Rol").setValue("User")
                                                                val userAux =
                                                                    Usuario(name,
                                                                        "",
                                                                        email,
                                                                        "User",
                                                                        "")
                                                                saveUserOnSharedPreferences(userAux)
                                                                goHome()
                                                            } catch (e: Exception) {
                                                                e.printStackTrace()
                                                            }
                                                        }
                                                        val parameters = Bundle()
                                                        parameters.putString(
                                                            "fields",
                                                            "name,email,id,picture.type(large)"
                                                        )
                                                        request.parameters = parameters
                                                        request.executeAsync()
                                                    }else{
                                                        val hash = it.value as HashMap<*, *>
                                                        val user = mapHashToUser(hash)
                                                        saveUserOnSharedPreferences(user)
                                                        if(user.rol == "User"){
                                                            goHome()
                                                        }
                                                    }
                                                }
                                        }else{
                                            progressBar.visibility = View.GONE
                                            Toast.makeText(this@LoginActivity, R.string.error_login, Toast.LENGTH_LONG).show()
                                        }
                                    }
                    }
                }

                override fun onCancel() {
                    Toast.makeText(this@LoginActivity, R.string.error_login, Toast.LENGTH_LONG).show()
                }

                override fun onError(error: FacebookException?) {
                    Toast.makeText(this@LoginActivity, R.string.error_login, Toast.LENGTH_LONG).show()
                }
            })
        }
        if(auth.currentUser != null){
            goHome()
        }
    }

    fun forgotPassword(view: View){
        startActivity(Intent(this, ForgotPassActivity::class.java))
    }

    fun registerLogin(view: View){
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode,resultCode,data)
    }

    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try{
                val account = task.getResult(ApiException::class.java)
                account.displayName
                if(account != null){
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    auth.signInWithCredential(credential)
                        .addOnCompleteListener(this){
                                task ->
                                    if(task.isSuccessful){
                                        dbReference.child(auth.currentUser?.uid.toString())
                                            .get().addOnSuccessListener {
                                                if(it.value == null){
                                                    val userDB = dbReference.child(auth.currentUser?.uid.toString())
                                                    userDB.child("Nombre").setValue(account.displayName)
                                                    userDB.child("Email").setValue(account.email)
                                                    userDB.child("Imagen").setValue(account.photoUrl?.toString())
                                                    userDB.child("Rol").setValue("User")

                                                    val userAux =
                                                        Usuario(account.givenName,
                                                            account.familyName,
                                                            account.email,
                                                            "User",
                                                            account.photoUrl?.toString())
                                                    saveUserOnSharedPreferences(userAux)

                                                    goHome()
                                                }else{
                                                    val hash = it.value as HashMap<*, *>
                                                    val user = mapHashToUser(hash)
                                                    saveUserOnSharedPreferences(user)
                                                    if(user.rol == "User"){
                                                        goHome()
                                                    }
                                                }
                                            }
                                    }else{
                                        progressBar.visibility = View.GONE
                                        Toast.makeText(this, R.string.error_login, Toast.LENGTH_LONG).show()
                                    }
                        }
                }
            }
            catch (e: ApiException){

            }


        }
    }

    private fun loginUser(){
        val mail:String = txtMail.text.toString()
        val pass:String = txtPassword.text.toString()

        if(!TextUtils.isEmpty(mail) && !TextUtils.isEmpty(pass)){
            progressBar.visibility = View.VISIBLE
            auth.signInWithEmailAndPassword(mail,pass)
                .addOnCompleteListener(this){
                        task ->
                        if(task.isSuccessful){
                            dbReference.child(auth.currentUser?.uid.toString())
                                .get().addOnSuccessListener {
                                    val hash = it.value as HashMap<*, *>
                                    val user = mapHashToUser(hash)
                                    saveUserOnSharedPreferences(user)
                                    if(user.rol == "User"){
                                        goHome()
                                    }
                                }
                        }else{
                            progressBar.visibility = View.GONE
                            Toast.makeText(this, R.string.error_login, Toast.LENGTH_LONG).show()
                        }
                }
        }
    }

    fun mapHashToUser(hash: HashMap<*, *>): Usuario {
        var nombreAux: String? = null
        var apellidoAux: String? = null
        var emailAux: String? = null
        var rolAux: String? = null
        var imgAux: String? = null

        if (hash["Nombre"] != null) nombreAux = hash["Nombre"].toString()
        if (hash["Apellido"] != null) apellidoAux = hash["Apellido"].toString()
        if (hash["Email"] != null) emailAux = hash["Email"].toString()
        if (hash["Rol"] != null) rolAux = hash["Rol"].toString()
        if (hash["Imagen"] != null) imgAux = hash["Imagen"].toString()

        return Usuario(
            nombreAux,
            apellidoAux,
            emailAux,
            rolAux,
            imgAux
        )
    }

    fun saveUserOnSharedPreferences (user: Usuario){
        val prefs = getSharedPreferences("Usuario", Context.MODE_PRIVATE).edit()
        prefs.putString("Nombre", user.nombre)
        prefs.putString("Apellido", user.apellido)
        prefs.putString("Email", user.email)
        prefs.putString("Rol", user.rol)
        prefs.putString("Imagen", user.img)
        prefs.apply()
    }

    private fun goHome(){
        val intent = Intent(this,MasterActivity::class.java)
        startActivity(intent)
        finish()
    }
}
