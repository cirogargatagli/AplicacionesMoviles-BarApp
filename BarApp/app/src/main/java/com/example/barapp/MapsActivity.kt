package com.example.barapp

import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.barapp.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val direccion = this.intent.extras?.getString("Direccion").toString()
        val nombre = this.intent.extras?.getString("Nombre").toString()

        val coder = Geocoder(this)
        var address: List<Address>
        try{
            address = coder.getFromLocationName(direccion, 5)
            if (address == null)
                return
            val location = address[0]

            val latLngBar = LatLng(location.latitude, location.longitude)
            mMap.addMarker(MarkerOptions().position(latLngBar).title(nombre))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngBar))
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15f))
        }
        catch (e: Exception){}
    }

//    private fun isLocationPermissionsGranted() : Boolean{
//        return ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//    }
//
//    private fun enableLocation(){
//        if(!::mMap.isInitialized) return
//        if(isLocationPermissionsGranted()){
//            mMap.isMyLocationEnabled = true
//        }else{
//
//        }
//    }
//
//    private fun requestLocationPermission(){
//        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
//            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
//        }else{
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        when(requestCode){
//            REQUEST_CODE_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                mMap.isMyLocationEnabled = true
//            }else{
//                Toast.makeText(this, "Para activar la localización ve a ajustes.", Toast.LENGTH_SHORT).show()
//            }
//            else -> {}
//        }
//    }
}