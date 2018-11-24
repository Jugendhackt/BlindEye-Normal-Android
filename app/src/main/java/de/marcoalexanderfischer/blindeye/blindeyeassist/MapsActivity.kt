package de.marcoalexanderfischer.blindeye.blindeyeassist

import android.annotation.SuppressLint
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task

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
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?;
        // locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
    }


    /**
     * Manipulates the map once available.mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bla))
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Do other setup activities here too, as described elsewhere in this tutorial.


        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        // Add a marker in Sydney and move the camera
        // val sydney = LatLng(-34.0, 151.0)
        // mMap.addMarker(MarkerOptions().position(location).title("Your last known location"))
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
    }

    fun getDeviceLocation() {
        var locationTask : Task<Location> = mFusedLocationProviderClient.lastLocation
        locationTask.addOnCompleteListener(this) {
            if(it.isSuccessful) {
                var location = it.result
                var latLng : LatLng = LatLng(location!!.latitude, location.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))

                mMap.addMarker(MarkerOptions().position(latLng).title("Your last known location"))
            }
        }
    }
}
