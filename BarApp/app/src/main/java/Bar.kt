class Bar(id: String?,nombre: String, direccion: String, img: String, valoracion: Double, agenda: Agenda, lat : Double?, lng : Double? ) {
    var id: String? = ""
    var nombre: String = ""
    var direccion: String = ""
    var img: String = ""
    var valoracion: Double = 0.0
    var agenda: Agenda? = null
    var lat: Double? = null
    var lng : Double? = null

    init {
        this.id = id
        this.nombre = nombre
        this.direccion = direccion
        this.img =img
        this.valoracion = valoracion
        this.agenda = agenda
        this.lat = lat
        this.lng = lng
    }
}