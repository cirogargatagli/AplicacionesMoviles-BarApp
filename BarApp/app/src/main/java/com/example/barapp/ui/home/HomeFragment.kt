package com.example.barapp.ui.home

import android.content.Intent
import android.net.Uri
import com.example.barapp.entity.Agenda
import com.example.barapp.entity.Bar
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.core.view.MenuItemCompat.getActionView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.barapp.MapsActivity
import com.example.barapp.adapter.ItemAdapter
import com.example.barapp.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.barapp.R

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var  dbReference: DatabaseReference
    private lateinit var  database: FirebaseDatabase
    private lateinit var adapter : ItemAdapter

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
                    adapter = ItemAdapter(bares as ArrayList<Bar>, root.findViewById(R.id.imagenSinBusqueda), root.findViewById(R.id.textSinBusqueda))
                    recycler.adapter = adapter
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

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                Log.d("onQueryTextChange", "query: " + query)
                adapter?.filter?.filter(query)
                return true
            }
        })

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                adapter?.filter?.filter("")
                return true
            }

            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                return true
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }
}