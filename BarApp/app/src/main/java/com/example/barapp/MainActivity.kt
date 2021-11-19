package com.example.barapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth

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
        dbReference = database.reference.child("com.example.barapp.entity.Bar")
        //showBares()
    }

//    private fun showBares() {
//        //Inicializo la lista de Bares
//        var bares = mutableListOf<com.example.barapp.entity.Bar>()
//
//        dbReference.addListenerForSingleValueEvent(
//            object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    //Get map of users in datasnapshot
//                    dataSnapshot.children.forEach {
//                        val barObj = it.getValue() as HashMap<*, *>
//                        val agendaObj = barObj["com.example.barapp.entity.Agenda"] as HashMap<*,*>
//
//                        var bar = com.example.barapp.entity.Bar(barObj["Nombre"].toString(),
//                            barObj["Direccion"].toString(),
//                            barObj["Imagen"].toString(),
//                            barObj["Valoracion"].toString().toDouble(),
//                            com.example.barapp.entity.Agenda(agendaObj["Dias"].toString(), agendaObj["Inicio"].toString() , agendaObj["Fin"].toString()))
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
