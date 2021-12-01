package com.example.barapp.ui.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.example.barapp.entity.Agenda
import com.example.barapp.entity.Bar
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.barapp.MapsActivity
import com.example.barapp.MasterActivity
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

                        val lat = barObj["Latitud"]
                        val lng = barObj["Longitud"]

                        var bar = Bar(it.key,
                            barObj["Nombre"].toString(),
                            barObj["Direccion"].toString(),
                            barObj["Imagen"].toString(),
                            barObj["Capacidad"] as Long,
                            barObj["Valoracion"].toString().toDouble(),
                            barObj["Facebook"].toString(),
                            Uri.parse(barObj["Instagram"].toString()),
                            barObj["Telefono"].toString(),
                            Agenda(agendaObj["Dias"].toString(), agendaObj["Inicio"].toString() , agendaObj["Fin"].toString()),
                            lat?.toString()?.toDouble(),
                            lng?.toString()?.toDouble()
                            )
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
        _binding!!.btnVerMapa.setOnClickListener{
            val intent = Intent(it.context, MapsActivity::class.java)
            intent.putExtra("allBars", true)
            startActivity(intent)
        }
    }
}