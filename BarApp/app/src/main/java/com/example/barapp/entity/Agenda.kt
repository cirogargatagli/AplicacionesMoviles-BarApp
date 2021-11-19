package com.example.barapp.entity

class Agenda(dias: String, inicio: String, fin: String) {
    var dias: String = ""
    var inicio: String = ""
    var fin: String = ""

    init {
        this.dias = dias
        this.inicio = inicio
        this.fin = fin
    }
}