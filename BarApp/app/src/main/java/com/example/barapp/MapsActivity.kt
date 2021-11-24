package com.example.barapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.barapp.databinding.ActivityMapsBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLngBounds
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var  dbReference: DatabaseReference
    private lateinit var  database: FirebaseDatabase

    companion object{
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        enableLocation()
        mMap.setOnMyLocationButtonClickListener(this)
    }

    private fun isLocationPermissionsGranted() : Boolean{
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun enableLocation(){
        if(!::mMap.isInitialized) return
        if(isLocationPermissionsGranted()){
            mMap.isMyLocationEnabled = true
            preMarkers()
        }else{
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
        }
    }

    private fun preMarkers(){
        val direccion = this.intent.extras?.getString("Direccion").toString()
        val lat = this.intent.extras?.getDouble("Latitud")
        val lng = this.intent.extras?.getDouble("Longitud")
        val id = this.intent.extras?.getString("Id")
        val coder = Geocoder(this)
        var address: List<Address>
        try{
            if (lat == 0.0 || lng == 0.0 ){
                address = coder.getFromLocationName(direccion, 10)
                if (address == null) return
                else{
                    val location = address[0]
                    var latLngBar = LatLng(location.latitude, location.longitude)
                    if (id != null) {
                        saveLatAndLngOnBar(id,location.latitude,location.longitude)
                    }
                    showMarkers(latLngBar)
                }
            }else{
                var latLngBar = LatLng(lat!!, lng!!)
                showMarkers(latLngBar)
            }
        }
        catch (e: Exception){
            startActivity(Intent(this, MasterActivity::class.java))
        }
    }

    private fun showMarkers(latLngBar : LatLng){
        val nombre = this.intent.extras?.getString("Nombre").toString()
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                val latLngUser = location?.let { LatLng(it.latitude, location.longitude) }
                val constructor = LatLngBounds.Builder()
                constructor.include(latLngBar)
                if (latLngUser != null) {
                    constructor.include(latLngUser)
                    val limites = constructor.build()

                    val ancho = resources.displayMetrics.widthPixels
                    val alto = resources.displayMetrics.heightPixels
                    val padding = (alto * 0.25).toInt()

                    mMap.addMarker(MarkerOptions().position(latLngBar).title(nombre))
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(limites, ancho, alto, padding))
                }
            }
    }

    private fun saveLatAndLngOnBar(id:String, lat : Double, lng : Double){
        database = FirebaseDatabase.getInstance()
        dbReference = database.reference.child("com.example.barapp.entity.Bar")
        dbReference.child(id).child("Latitud").setValue(lat)
        dbReference.child(id).child("Longitud").setValue(lng)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_CODE_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                mMap.isMyLocationEnabled = true
                preMarkers()
            }else{
                Toast.makeText(this, "Para activar la localización ve a ajustes.", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if(!::mMap.isInitialized) return
        if(!isLocationPermissionsGranted()){
            mMap.isMyLocationEnabled = false
            Toast.makeText(this, "Para activar la localización ve a ajustes.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }
}