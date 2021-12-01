package com.example.barapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.barapp.adapter.ItemAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.barapp.databinding.ActivityMapsBinding
import com.example.barapp.entity.Agenda
import com.example.barapp.entity.Bar
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLngBounds
import com.google.firebase.database.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var  dbReference: DatabaseReference
    private lateinit var  database: FirebaseDatabase

    companion object{
        const val REQUEST_CODE_LOCATION = 0
    }

    private var allBars = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        allBars = intent.getBooleanExtra("allBars", false)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if(allBars){
            showAllMarkers()
        }else{
            enableLocation()
        }

        mMap.setOnMyLocationButtonClickListener(this)
    }

    private fun enableLocation(){
        if(!::mMap.isInitialized) return
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap.isMyLocationEnabled = true
            preMarkers()
        }else{
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this, R.string.modify_permissions, Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, R.string.error_location, Toast.LENGTH_LONG).show()
            startActivity(Intent(this, MasterActivity::class.java))
        }
    }

    private fun showAllMarkers(){
        database = FirebaseDatabase.getInstance()
        dbReference = database.reference.child("Bar")
        dbReference.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val latsAndLngs = mutableListOf<LatLng>()
                    dataSnapshot.children.forEach {
                        val barObj = it.value as HashMap<*, *>

                        val lat = barObj["Latitud"].toString().toDouble()
                        val lng = barObj["Longitud"].toString().toDouble()
                        val nombre = barObj["Nombre"].toString()
                        val id = it.key
                        val direccion = barObj["Direccion"].toString()

                        val coder = Geocoder(this@MapsActivity)
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
                                    latsAndLngs.add(latLngBar)
                                    mMap.addMarker(MarkerOptions().position(latLngBar).title(nombre))
                                }
                            }else{
                                var latLngBar = LatLng(lat, lng)
                                latsAndLngs.add(latLngBar)
                                mMap.addMarker(MarkerOptions().position(latLngBar).title(nombre))
                            }
                        }
                        catch (e: Exception){
                            allBars = false
                            startActivity(Intent(this@MapsActivity, MasterActivity::class.java))
                        }
                    }
                    val constructor = LatLngBounds.Builder()
                    latsAndLngs.forEach {
                        constructor.include(it)
                    }
                    val limites = constructor.build()
                    val ancho = resources.displayMetrics.widthPixels
                    val alto = resources.displayMetrics.heightPixels
                    val padding = (alto * 0.2).toInt()
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(limites,ancho, alto, padding))
                    allBars = false
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    //handle databaseError
                }
            })
    }

    private fun showMarkers(latLngBar : LatLng){
        val nombre = this.intent.extras?.getString("Nombre").toString()
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    if(location == null){
                        Toast.makeText(this, R.string.activate_location, Toast.LENGTH_SHORT).show()
                        mMap.addMarker(MarkerOptions().position(latLngBar).title(nombre))
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLngBar))
                    } else{
                        val latLngUser = location?.let { LatLng(it.latitude, location.longitude) }
                        val constructor = LatLngBounds.Builder()
                        constructor.include(latLngBar)
                        constructor.include(latLngUser)
                        val limites = constructor.build()

                        val ancho = resources.displayMetrics.widthPixels
                        val alto = resources.displayMetrics.heightPixels
                        val padding = (alto * 0.2).toInt()

                        mMap.addMarker(MarkerOptions().position(latLngBar).title(nombre))
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(limites, ancho, alto, padding))
                    }
                }
        }else{
            requestLocationPermission()
        }
    }

    private fun saveLatAndLngOnBar(id:String, lat : Double, lng : Double){
        database = FirebaseDatabase.getInstance()
        dbReference = database.reference.child("Bar")
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
            REQUEST_CODE_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mMap.isMyLocationEnabled = true
                preMarkers()
            }else{
                Toast.makeText(this, R.string.modify_permissions, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if(!::mMap.isInitialized) return
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            mMap.isMyLocationEnabled = false
            Toast.makeText(this, R.string.modify_permissions, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }
}