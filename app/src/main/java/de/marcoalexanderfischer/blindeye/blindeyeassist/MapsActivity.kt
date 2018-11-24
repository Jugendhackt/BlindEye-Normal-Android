package de.marcoalexanderfischer.blindeye.blindeyeassist

import android.annotation.SuppressLint
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    // private var locationManager : LocationManager? = null

    private lateinit var mMap: GoogleMap
    private lateinit var location : Location
    private lateinit var mFusedLocationProviderClient : FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.setOnMapClickListener {
            mMap.addMarker(MarkerOptions().position(it).title("Neues Hindernis"))
        }
        // Do other setup activities here too, as described elsewhere in this tutorial.


        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        // Add a marker in Sydney and move the camera
        // val sydney = LatLng(-34.0, 151.0)
        // mMap.addMarker(MarkerOptions().position(location).title("Your last known location"))
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
    }

    @SuppressLint("MissingPermission")
    fun getDeviceLocation() {
        val request: LocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(1000)

        val locationProvider = ReactiveLocationProvider(this)
        locationProvider.getUpdatedLocation(request)
            .subscribe {
                val lat = it.latitude
                val long = it.longitude
                var latLng = LatLng(lat, long)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
                mMap.addMarker(MarkerOptions().position(latLng).title("Your last known location"))
            }
    }
}
