class Bar(nombre: String, direccion: String, img: String, valoracion: Double, agenda: Agenda ) {
    var nombre: String = ""
    var direccion: String = ""
    var img: String = ""
    var valoracion: Double = 0.1
    var agenda: Agenda? = null

    init {
        this.nombre = nombre
        this.direccion = direccion
        this.img =img
        this.valoracion = valoracion
        this.agenda = agenda
    }

}