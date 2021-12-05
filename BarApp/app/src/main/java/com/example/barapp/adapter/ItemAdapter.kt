package com.example.barapp.adapter

import android.Manifest
import android.app.Activity
import com.example.barapp.entity.Bar
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.barapp.MapsActivity
import com.example.barapp.R
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat
import com.facebook.FacebookSdk.getApplicationContext
import android.app.PendingIntent
import android.content.*
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ColorSpace
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.example.barapp.DatePickerFragment
import com.example.barapp.MasterActivity
import com.example.barapp.entity.Reserva
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlin.collections.ArrayList


class ItemAdapter(
    private var dataset: ArrayList<Bar>
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>(), Filterable {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just an Affirmation object.

    var baresFilterList = ArrayList<Bar>()
    var datasetAux = ArrayList<Bar>()

    init {
        baresFilterList = dataset
        datasetAux = dataset
    }

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.item_title)
        val imageView: ImageView = view.findViewById(R.id.item_image)
        val textAddress: TextView = view.findViewById(R.id.item_address)
        val lineAddress : View = view.findViewById(R.id.line_address)
        val textPhone : TextView = view.findViewById(R.id.item_phone)
        val textCapacity : TextView = view.findViewById(R.id.item_capacity)
        val ratingBar : RatingBar = view.findViewById(R.id.ratingBar)
        val textRating : TextView = view.findViewById(R.id.item_rating)
        val linePhone : View = view.findViewById(R.id.line_phone)
        val editTextDate : EditText = view.findViewById(R.id.et_date)
        val btnReservar : Button = view.findViewById(R.id.btnReservar)
        val facebook : ImageView = view.findViewById(R.id.iconFacebook)
        val instagram : ImageView = view.findViewById(R.id.iconInstagram)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        val auth = FirebaseAuth.getInstance()
        val dbReferenceBar = FirebaseDatabase.getInstance().reference.child("Bar").child(item.id.toString()).child("Agenda")
        val dbReferenceUsuario = FirebaseDatabase.getInstance().reference.child("Usuario").child(auth.currentUser?.uid.toString()).child("Reservas")
        var dbReferenceDateSelected : DatabaseReference = dbReferenceBar
        var dateSelected : String? = null

        Picasso.get()
            .load(item.img)
            .into(holder.imageView)
        holder.textView.text = item.nombre
        holder.ratingBar.rating = item.valoracion.toFloat()
        holder.textRating.text = item.valoracion.toString()
        holder.textAddress.text = item.direccion
        holder.lineAddress.setOnClickListener{
            val intent = Intent(it.context, MapsActivity::class.java)
            intent.putExtra("Id",item.id)
            intent.putExtra("Direccion", item.direccion)
            intent.putExtra("Nombre", item.nombre)
            intent.putExtra("Latitud", item.lat)
            intent.putExtra("Longitud", item.lng)
            startActivity(it.context, intent, Bundle())
        }

        val popupMenu = PopupMenu( holder.imageView.context, holder.imageView)
        popupMenu.inflate(R.menu.menu_contextual)
        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.option_download -> {
                    val thread = Thread {
                        try {
                            val bitmap = Picasso.get().load(item.img).get()
                            val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                            val myPath = File(directory, item.nombre+".png")
                            var fos: FileOutputStream? = null

                            try {
                                fos = FileOutputStream(myPath)
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                try {
                                    fos!!.close()

                                    val notificationChannel = NotificationChannel("1", item.nombre, NotificationManager.IMPORTANCE_LOW).apply {
                                        enableLights(true)
                                        enableVibration(true)
                                        this.description = description
                                        lightColor = Color.BLUE
                                        vibrationPattern = longArrayOf(0, 500, 500, 500, 500)
                                    }
                                    val manager = getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                                    manager.createNotificationChannel(notificationChannel)
                                    val notification = NotificationCompat.Builder(getApplicationContext(), notificationChannel.id)

                                    val intent = Intent(Intent.ACTION_VIEW, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                                    intent.data = Uri.fromFile(myPath)
                                    intent.type = "image/*"
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                                    val pendingIntent = PendingIntent.getActivity(
                                        getApplicationContext(), 0,
                                        intent, 0
                                    )

                                    notification.setAutoCancel(true)
                                        .setColor(ContextCompat.getColor(getApplicationContext(), R.color.primary))
                                        .setSmallIcon(R.drawable.ic_baseline_file_download_done_24)
                                        .setWhen(System.currentTimeMillis())
                                        .setContentTitle(item.nombre + ".png")
                                        .setContentText(getApplicationContext().getString(R.string.download_succesfully))
                                        .setContentIntent(pendingIntent)
                                    manager.notify(1, notification.build())
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            }
                        } catch (e: Exception) {
                        }
                    }

                    thread.start()

                    return@setOnMenuItemClickListener true
                }
                R.id.option_share -> {
                    val thread = Thread {
                        try {
                            val bitmap = Picasso.get().load(item.img).get()
                            try {
                                val file: File =
                                    File(getApplicationContext().cacheDir, item.nombre + ".png")
                                var fOut: FileOutputStream? = null
                                fOut = FileOutputStream(file)
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
                                fOut.flush()
                                fOut.close()
                                file.setReadable(true, false)
                                val intent = Intent(Intent.ACTION_SEND)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                val imageUri = FileProvider.getUriForFile(getApplicationContext(), "com.example.barapp.provider", file)
                                intent.putExtra(Intent.EXTRA_STREAM, imageUri)
                                intent.type = "image/png"
                                getApplicationContext().startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        } catch (e: Exception) {
                        }
                    }

                    thread.start()

                    return@setOnMenuItemClickListener true
                }
                else -> return@setOnMenuItemClickListener false
            }
        }


        holder.imageView.setOnLongClickListener {
            popupMenu.show()
            return@setOnLongClickListener true
        }

        holder.textPhone.text = item.telefono

        holder.linePhone.setOnClickListener{
            val i = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+" + item.telefono))
            startActivity(it.context, i, Bundle())
        }

        holder.editTextDate.setOnClickListener{
            val datePicker = DatePickerFragment{ day, month, year ->
                dateSelected = day.toString() + "-" + (month + 1).toString() + "-" + year.toString()
                holder.editTextDate.setText(dateSelected)
                holder.editTextDate.error = null

                dbReferenceDateSelected = dbReferenceBar.child(dateSelected!!)
                dbReferenceDateSelected.get().addOnSuccessListener {
                        if (it.value == null){
                            holder.btnReservar.isEnabled = true
                        }else{
                            dbReferenceDateSelected.child("MesasOcupadas").get().addOnSuccessListener {
                                var mesasOcupadas = it.value.toString().toInt()

                                if(mesasOcupadas >= item.capacidad){
                                    val msgError = item.nombre + " " + getApplicationContext().getString(R.string.exceeds_capacity) + " " + dateSelected
                                    holder.editTextDate.error = msgError
                                    Toast.makeText(getApplicationContext(), msgError, Toast.LENGTH_SHORT).show()
                                    holder.btnReservar.isEnabled = false
                                }
                            }
                        }
                    }
                dbReferenceUsuario.child(dateSelected + item.nombre).get().addOnSuccessListener{
                    if (it.value == null){
                        holder.btnReservar.isEnabled = true
                    } else{
                        val msgError = getApplicationContext().getString(R.string.exist_reserve) + " " + item.nombre + " " + getApplicationContext().getString(R.string.he) + " " + dateSelected
                        holder.editTextDate.error = msgError
                        Toast.makeText(getApplicationContext(),  msgError, Toast.LENGTH_SHORT).show()
                        holder.btnReservar.isEnabled = false
                    }
                }
             }
            val activity = it.context as MasterActivity
            datePicker.show(activity.supportFragmentManager, "datePicker")
        }

        holder.btnReservar.setOnClickListener {
            dbReferenceDateSelected.get().addOnSuccessListener {
                if (it.value == null){
                    dbReferenceDateSelected.child("MesasOcupadas").setValue(1)
                }else{
                    dbReferenceDateSelected.child("MesasOcupadas").get().addOnSuccessListener {
                        val increment = it.value.toString().toInt() + 1
                        dbReferenceDateSelected.child("MesasOcupadas").setValue(increment)
                    }
                }
            }
            val reserva = Reserva(dateSelected, item.nombre,item.direccion, item.img, item.telefono, item.facebook, item.instagram.toString(), item.id.toString())
            dbReferenceUsuario.child(dateSelected + item.nombre).setValue(reserva)

            holder.editTextDate.setText("")
            holder.btnReservar.isEnabled = false

            val view = View.inflate(getApplicationContext(), R.layout.dialog_view, null)
            val builder = AlertDialog.Builder(holder.btnReservar.context)
            builder.setView(view)
            val dialog = builder.create()
            dialog.show()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setCancelable(false)

            view.findViewById<TextView>(R.id.text_dialog).text =
                getApplicationContext().getString(R.string.dialog_text1) + " " + item.nombre + " " + getApplicationContext().getString(R.string.dialog_text2) + " " +  dateSelected

            val btnConfirmar = view.findViewById<Button>(R.id.btn_confirm)

            btnConfirmar.setOnClickListener {
                dialog.dismiss()
            }
        }

        holder.textCapacity.text = item.capacidad.toString()

        holder.facebook.setOnClickListener{
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + item.facebook))
                startActivity(it.context, intent, Bundle())
            } catch (e: Exception) {
                startActivity(
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
                startActivity(it.context, forApp, null)
            } catch (e: ActivityNotFoundException) {
                startActivity(it.context, forBrowser, null)
            }
        }
    }

    /**
     * Return the size of your dataset (invoked by the layout manager)
     */
    override fun getItemCount() = dataset.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    dataset = datasetAux
                    baresFilterList = datasetAux as ArrayList<Bar>
                } else {
                    val resultList = ArrayList<Bar>()
                    for (row in datasetAux) {
                        if (row.nombre.lowercase().contains(constraint.toString().lowercase())) {
                            resultList.add(row)
                        }
                    }
                    baresFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = baresFilterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                baresFilterList = results?.values as ArrayList<Bar>
                dataset = baresFilterList
                notifyDataSetChanged()
            }
        }
    }
}
