package com.example.barapp.ui.reservations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.barapp.adapter.ReservaAdapter
import com.example.barapp.databinding.FragmentReservationBinding
import com.example.barapp.entity.Bar
import com.example.barapp.entity.Reserva
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

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

        showReservas()

        return root
    }

    private fun showReservas() {
        var reservasMutable = mutableListOf<Reserva>()

        dbReference.child(auth.currentUser!!.uid).get().addOnSuccessListener {
            val usuario = it.value as HashMap<*,*>
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
                    mapaReserva["instagram"].toString()
                )
                reservasMutable.add(reserva)
            }
            val recycler = binding.recyclerViewReserva
            recycler.adapter = ReservaAdapter(reservasMutable)
            recycler.setHasFixedSize(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}