package com.example.barapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.barapp.entity.Usuario
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import java.io.File
import java.lang.Exception
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException




class RegisterActivity : AppCompatActivity() {

    private lateinit var  imgPerfil: ImageView
    private lateinit var  imgPerfilUri: Uri
    private lateinit var  txtName: TextInputEditText
    private lateinit var  txtLastName:TextInputEditText
    private lateinit var  txtEmail:TextInputEditText
    private lateinit var  txtPassword:TextInputEditText
    private lateinit var  progressBar:ProgressBar
    private lateinit var  dbReference: DatabaseReference
    private lateinit var  database:FirebaseDatabase
    private lateinit var  auth:FirebaseAuth
    private lateinit var  storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        imgPerfil = findViewById(R.id.img_perfil)
        txtName = findViewById(R.id.txtName_edit_text)
        txtLastName = findViewById(R.id.txtLastName_edit_text)
        txtEmail = findViewById(R.id.txtEmail_edit_text)
        txtPassword = findViewById(R.id.txtPassword_edit_text)
        imgPerfilUri = Uri.parse("default")

        imgPerfil.setOnClickListener{
            val intent = Intent(CropImage.getPickImageChooserIntent(this))
            resultLauncher.launch(intent)
        }

        progressBar = findViewById(R.id.progressBarRegister)
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        dbReference = database.reference.child("Usuario")
    }

    fun register(view:View){
        createNewAccount()
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imgPerfilUri =  CropImage.getPickImageResultUri(this,result.data)
            Picasso.get()
                .load(imgPerfilUri)
                .into(imgPerfil)
        }
    }

    private fun validateForm(name:String, lastName: String, email:String,password:String) : String{
        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            return getString(R.string.complete_all)
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return getString(R.string.into_valid_mail)
        }
        if(password.length < 6){
            return getString(R.string.short_password)
        }
        return ""
    }

    private fun createNewAccount(){
        val name:String=txtName.text.toString()
        val lastName:String=txtLastName.text.toString()
        val email:String=txtEmail.text.toString()
        val password:String=txtPassword.text.toString()

        val resultValidation = validateForm(name,lastName,email,password)

        if(resultValidation == ""){
            progressBar.visibility = View.VISIBLE

            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this) {
                    task ->
                    if (!task.isSuccessful) {
                        try {
                            progressBar.visibility = View.GONE
                            throw task.exception!!
                        } catch (e: FirebaseAuthWeakPasswordException) {
                            Toast.makeText(this, R.string.weak_password, Toast.LENGTH_SHORT).show()
                        } catch (e: FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(this, R.string.invalid_mail, Toast.LENGTH_SHORT).show()
                        } catch (e: FirebaseAuthUserCollisionException) {
                            Toast.makeText(this, R.string.collision_mail, Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(this, R.string.error_register, Toast.LENGTH_SHORT).show()
                        }
                    }
                    if(task.isComplete && auth.currentUser != null){
                        val user: FirebaseUser? = auth.currentUser
                        verifyEmail(user)
                        val userDB = dbReference.child(user?.uid.toString())

                        userDB.child("Nombre").setValue(name)
                        userDB.child("Apellido").setValue(lastName)
                        userDB.child("Email").setValue(email)
                        userDB.child("Rol").setValue("User")
                        if (imgPerfilUri.toString() != "default") {
                            val file = File(this.filesDir, auth.currentUser!!.uid)
                            val bytes = imgPerfilUri.let { this.contentResolver.openInputStream(it)?.readBytes() }!!
                            file.writeBytes(bytes)
                            storageReference = FirebaseStorage.getInstance().reference.child(auth.currentUser?.uid.toString())
                            storageReference.putFile(imgPerfilUri).addOnSuccessListener {
                                if (it.task.isComplete) {
                                    val result = it.metadata?.reference?.downloadUrl
                                    result?.addOnSuccessListener {
                                        val downloadUri = it.toString()
                                        userDB.child("Imagen").setValue(downloadUri)
                                        val userAux = Usuario(name,lastName,email,"User", downloadUri)
                                        saveUserOnSharedPreferences(userAux)
                                        progressBar.visibility = View.GONE
                                        action()
                                    }
                                }
                            }
                        } else{
                            val userAux = Usuario(name,lastName,email,"User", null)
                            saveUserOnSharedPreferences(userAux)
                            progressBar.visibility = View.GONE
                            action()
                        }
                    }
                }
        } else{
            Toast.makeText(this, resultValidation, Toast.LENGTH_SHORT).show()
        }
    }

    private fun action(){
        startActivity(Intent(this, MasterActivity::class.java))
        finish()
    }

    private fun verifyEmail(user:FirebaseUser?){
        user?.sendEmailVerification()
            ?.addOnCompleteListener(this){
                task ->
                if(task.isComplete){
                    Toast.makeText(this, R.string.send_mail, Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this, R.string.error_send_mail, Toast.LENGTH_SHORT).show()
                }
            }
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
}