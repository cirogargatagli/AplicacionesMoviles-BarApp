package com.example.barapp.adapter

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.barapp.MapsActivity
import com.example.barapp.R
import com.example.barapp.entity.Reserva
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import java.lang.Exception

class ReservaAdapter(
    private var dataset: ArrayList<Reserva>
) : RecyclerView.Adapter<ReservaAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.item_title_reserva)
        //val imageView: ImageView = view.findViewById(R.id.item_image_reserva)
        val textAddress: TextView = view.findViewById(R.id.item_address_reserva)
        val lineAddress : View = view.findViewById(R.id.line_address_reserva)
        val textPhone : TextView = view.findViewById(R.id.item_phone_reserva)
        val linePhone : View = view.findViewById(R.id.line_phone_reserva)
        val textDate : TextView = view.findViewById(R.id.item_calendar_reserva)
        val btnCancelarReservar : Button = view.findViewById(R.id.btnCancelarReserva)
        val facebook : ImageView = view.findViewById(R.id.iconFacebook_reserva)
        val instagram : ImageView = view.findViewById(R.id.iconInstagram_reserva)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_reserva, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        val auth = FirebaseAuth.getInstance()
        val dbReferenceBar = FirebaseDatabase.getInstance().reference.child("Bar").child(item.id!!).child("Agenda").child(item.fecha.toString()).child("MesasOcupadas")
        val dbReferenceUsuario = FirebaseDatabase.getInstance().reference.child("Usuario").child(auth.currentUser?.uid.toString()).child("Reservas")
        var dateSelected : String? = null

        /*Picasso.get()
            .load(item.imgBar)
            .into(holder.imageView)*/

        holder.textView.text = item.nombreBar

        holder.textAddress.text = item.direccionBar
        /*holder.lineAddress.setOnClickListener{
            val intent = Intent(it.context, MapsActivity::class.java)
            intent.putExtra("Direccion", item.direccionBar)
            intent.putExtra("Nombre", item.nombreBar)
            ContextCompat.startActivity(it.context, intent, Bundle())
        }*/

        holder.textPhone.text = item.telefonoBar
        holder.linePhone.setOnClickListener{
            val i = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+" + item.telefonoBar))
            ContextCompat.startActivity(it.context, i, Bundle())
        }

        holder.textDate.text = item.fecha

        holder.facebook.setOnClickListener{
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + item.facebook))
                ContextCompat.startActivity(it.context, intent, Bundle())
            } catch (e: Exception) {
                ContextCompat.startActivity(
                    it.context,
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://www.facebook.com/" + item.facebook)
                    ),
                    Bundle()
                )
            }
        }

        holder.instagram.setOnClickListener{
            val uriForApp: Uri = Uri.parse("http://instagram.com/_u/" + item.instagram)
            val forApp = Intent(Intent.ACTION_VIEW, uriForApp)

            val uriForBrowser: Uri = Uri.parse("http://instagram.com/" + item.instagram)
            val forBrowser = Intent(Intent.ACTION_VIEW, uriForBrowser)

            forApp.setPackage("com.instagram.android")

            try {
                ContextCompat.startActivity(it.context, forApp, null)
            } catch (e: ActivityNotFoundException) {
                ContextCompat.startActivity(it.context, forBrowser, null)
            }
        }

        holder.btnCancelarReservar.setOnClickListener {
            val builder = AlertDialog.Builder(holder.btnCancelarReservar.context)
            builder.setMessage(R.string.confirm_cancel_reserve)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm) { dialog, id ->
                    dbReferenceUsuario.child(item.fecha + item.nombreBar).removeValue().addOnSuccessListener {
                        dbReferenceBar.get().addOnSuccessListener {
                            val decrement = it.value.toString().toInt() - 1
                            dbReferenceBar.setValue(decrement)
                            Toast.makeText(holder.btnCancelarReservar.context, R.string.canceled_reserve, Toast.LENGTH_LONG).show()

                            removeAt(position)
                        }
                    }
                }
                .setNegativeButton(R.string.cancelar) { dialog, id ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
    }

    private fun removeAt(position: Int) {
        dataset.removeAt(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dataset.size);
    }

    override fun getItemCount() = dataset.size

}