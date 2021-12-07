package com.example.barapp.ui.reservations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.barapp.R
import com.example.barapp.adapter.ReservaAdapter
import com.example.barapp.databinding.FragmentReservationBinding
import com.example.barapp.entity.Bar
import com.example.barapp.entity.Reserva
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ReservationFragment : Fragment() {

    private lateinit var reservationViewModel: ReservationViewModel
    private var _binding: FragmentReservationBinding? = null
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
        reservationViewModel =
            ViewModelProvider(this).get(ReservationViewModel::class.java)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        dbReference = database.reference.child("Usuario")

        _binding = FragmentReservationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        showReservas(root)

        return root
    }

    private fun showReservas(root : View) {
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val dayAux = Date()
        val currentDay = sdf.format(dayAux)
        val day = sdf.parse(currentDay)

        var reservasMutable = mutableListOf<Reserva>()
        val textReserva : TextView = root.findViewById(R.id.sinReserva)
        val imagenReserva : ImageView = root.findViewById(R.id.imagenSinReserva)

        dbReference.child(auth.currentUser!!.uid).get().addOnSuccessListener {
            val usuario = it.value as HashMap<*,*>
            val reservasAux = usuario["Reservas"]
            if (reservasAux != null){
                textReserva.visibility = View.GONE
                imagenReserva.visibility = View.GONE
                val reservas= usuario["Reservas"] as HashMap<*,*>

                val listReservas = reservas.values

                listReservas.forEach{
                    val mapaReserva = it as HashMap<*,*>
                    var reserva = Reserva(
                        mapaReserva["fecha"].toString(),
                        mapaReserva["nombreBar"].toString(),
                        mapaReserva["direccionBar"].toString(),
                        mapaReserva["imgBar"].toString(),
                        mapaReserva["telefonoBar"].toString(),
                        mapaReserva["facebook"].toString(),
                        mapaReserva["instagram"].toString(),
                        mapaReserva["id"].toString()
                    )

                    val date = sdf.parse(reserva.fecha)
                    val newDate = Date(date.time + 86400000)

                    if(newDate.after(day)){
                        reservasMutable.add(reserva)
                    }
                }
                val recycler = binding.recyclerViewReserva
                recycler.adapter = ReservaAdapter(reservasMutable as ArrayList<Reserva>, imagenReserva, textReserva)
                recycler.setHasFixedSize(true)
            }
            if(reservasMutable.size == 0) {
                textReserva.visibility = View.VISIBLE
                imagenReserva.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}