package com.example.barapp

import Agenda
import Bar
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.ArrayList
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.DataSnapshot

import com.google.firebase.database.ValueEventListener

import com.google.firebase.database.FirebaseDatabase

import com.google.firebase.database.DatabaseReference




class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var  dbReference: DatabaseReference
    private lateinit var  database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        dbReference = database.reference.child("Bar")
        showBares()
    }

    private fun showBares() {
        dbReference.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    //Get map of users in datasnapshot
                    dataSnapshot.children.forEach {
                        var barObj = it.getValue() as HashMap<*, *>
                        var agendaObj = barObj["Agenda"] as HashMap<*,*>

                        var bar = Bar(barObj["Nombre"].toString(),
                            barObj["Direccion"].toString(),
                            barObj["Imagen"].toString(),
                            barObj["Valoracion"].toString().toDouble(),
                            Agenda(agendaObj["Dias"].toString(), agendaObj["Inicio"].toString() , agendaObj["Fin"].toString()))
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    //handle databaseError
                }
            })

            action()
    }


    fun logOut(view:View){
        auth.signOut()
        action()
    }

    private fun action(){
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
