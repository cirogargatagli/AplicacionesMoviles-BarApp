package com.example.barapp.ui.home

import Agenda
import Bar
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.barapp.LoginActivity
import com.example.barapp.R
import com.example.barapp.adapter.ItemAdapter
import com.example.barapp.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var  dbReference: DatabaseReference
    private lateinit var  database: FirebaseDatabase

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        dbReference = database.reference.child("Bar")

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        /*val btn : Button = binding.btnCerrarSesion
        btn.setOnClickListener{
            auth = FirebaseAuth.getInstance()
            auth.signOut()
            startActivity(Intent(context, LoginActivity::class.java))
            onDestroyView()
        }*/
        showBares()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showBares() {
        //Inicializo la lista de Bares
        var bares = mutableListOf<Bar>()

        dbReference.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    //Get map of users in datasnapshot
                    dataSnapshot.children.forEach {
                        val barObj = it.getValue() as HashMap<*, *>
                        val agendaObj = barObj["Agenda"] as HashMap<*,*>

                        var bar = Bar(barObj["Nombre"].toString(),
                            barObj["Direccion"].toString(),
                            barObj["Imagen"].toString(),
                            barObj["Valoracion"].toString().toDouble(),
                            Agenda(agendaObj["Dias"].toString(), agendaObj["Inicio"].toString() , agendaObj["Fin"].toString()))
                        //Agrego el bar a la lista
                        bares.add(bar)
                    }
                    val recycler = binding.recyclerView
                    recycler.adapter = ItemAdapter(bares)
                    recycler.setHasFixedSize(true)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    //handle databaseError
                }
            })

    }
}