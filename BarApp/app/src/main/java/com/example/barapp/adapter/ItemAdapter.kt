package com.example.barapp.adapter

import Bar
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.barapp.MapsActivity
import com.example.barapp.R
import com.squareup.picasso.Picasso
import org.w3c.dom.Text

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
        holder.textView.text = item.nombre
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
        Picasso.get()
            .load(item.img)
            .into(holder.imageView)
    }

    /**
     * Return the size of your dataset (invoked by the layout manager)
     */
    override fun getItemCount() = dataset.size
}