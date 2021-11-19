package com.example.barapp.adapter

import android.content.ActivityNotFoundException
import com.example.barapp.entity.Bar
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.barapp.MapsActivity
import com.example.barapp.R
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.ContextCompat.startActivity
import java.lang.Exception


class ItemAdapter(
    private val dataset: List<Bar>
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just an Affirmation object.

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
        val facebook : ImageView = view.findViewById(R.id.iconFacebook)
        val instagram : ImageView = view.findViewById(R.id.iconInstagram)
    }

    /**
     * Create new views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
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
        holder.textPhone.text = item.telefono
        holder.linePhone.setOnClickListener{
            val i = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+" + item.telefono))
            startActivity(it.context, i, Bundle())
        }
        holder.textCapacity.text = item.capacidad.toString()
        holder.facebook.setOnClickListener{
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + item.facebook))
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
}