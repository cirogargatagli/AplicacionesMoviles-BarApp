package com.example.barapp

import Agenda
import Bar
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.barapp.adapter.ItemAdapter
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
        //showBares()
    }

//    private fun showBares() {
//        //Inicializo la lista de Bares
//        var bares = mutableListOf<Bar>()
//
//        dbReference.addListenerForSingleValueEvent(
//            object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    //Get map of users in datasnapshot
//                    dataSnapshot.children.forEach {
//                        val barObj = it.getValue() as HashMap<*, *>
//                        val agendaObj = barObj["Agenda"] as HashMap<*,*>
//
//                        var bar = Bar(barObj["Nombre"].toString(),
//                            barObj["Direccion"].toString(),
//                            barObj["Imagen"].toString(),
//                            barObj["Valoracion"].toString().toDouble(),
//                            Agenda(agendaObj["Dias"].toString(), agendaObj["Inicio"].toString() , agendaObj["Fin"].toString()))
//                        //Agrego el bar a la lista
//                        bares.add(bar)
//                    }
//                }
//
//                override fun onCancelled(databaseError: DatabaseError) {
//                    //handle databaseError
//                }
//            })
//        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
//        recyclerView.adapter = ItemAdapter(bares)
//        recyclerView.setHasFixedSize(true)
//        action()
//    }


    fun logOut(view:View){
        auth.signOut()
        action()
    }

    private fun action(){
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
