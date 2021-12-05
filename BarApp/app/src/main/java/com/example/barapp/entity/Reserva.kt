package com.example.barapp.entity

import java.util.*

class Reserva(fecha : String?,
              nombreBar : String?,
              direccionBar : String?,
              imgBar : String?,
              telefonoBar : String?,
              facebook : String?,
              instagram : String,
              id : String) {
    var fecha: String? = null
    var nombreBar: String? = ""
    var direccionBar: String? = ""
    var imgBar: String? = ""
    var telefonoBar: String? = ""
    var facebook: String? = ""
    var instagram: String? = ""
    var id: String? = ""


    init {
        this.fecha = fecha
        this.nombreBar = nombreBar
        this.direccionBar = direccionBar
        this.imgBar = imgBar
        this.telefonoBar = telefonoBar
        this.facebook = facebook
        this.instagram = instagram
        this.id = id
    }
}