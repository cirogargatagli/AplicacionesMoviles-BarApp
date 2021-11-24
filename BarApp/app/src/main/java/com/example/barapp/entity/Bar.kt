package com.example.barapp.entity

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

class Bar(
    id: String?, nombre: String, direccion: String, img: String,
    capacidad: Long, valoracion: Double, facebook: String, instagram: Uri?,
    telefono: String, agenda: Agenda, lat: Double?, lng: Double? ){
    var id: String? = ""
    var nombre: String = ""
    var direccion: String = ""
    var img: String = ""
    var capacidad: Long = 0
    var valoracion: Double = 0.0
    var facebook : String = ""
    var instagram : Uri? = null
    var telefono : String = ""
    var agenda: Agenda? = null
    var lat: Double? = null
    var lng : Double? = null



    init {
        this.id = id
        this.nombre = nombre
        this.direccion = direccion
        this.img =img
        this.capacidad = capacidad
        this.valoracion = valoracion
        this.facebook = facebook
        this.instagram = instagram
        this.telefono = telefono
        this.agenda = agenda
        this.lat = lat
        this.lng = lng
    }
}