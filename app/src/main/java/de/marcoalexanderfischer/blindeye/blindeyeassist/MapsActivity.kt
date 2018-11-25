package de.marcoalexanderfischer.blindeye.blindeyeassist

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.StrictMode
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

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

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        sendGet("obstacles")
        mMap.setOnMapClickListener {
            displayAlert(it)
            mMap.addMarker(MarkerOptions().position(it).title("Neues Hindernis"))
        }
    }

    private fun sendPost(path: String, data: JSONObject) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val queue = Volley.newRequestQueue(this)
        val url = "http://192.168.137.1:4242/$path" // TODO: No hardcoded IP

        val request = JsonObjectRequest(Request.Method.POST, url, data, Response.Listener<JSONObject> {
            Toast.makeText(this, "Erfolgreich hochgeladen", Toast.LENGTH_LONG).show()
        }, Response.ErrorListener {
            Toast.makeText(this, "Es ist ein Fehler aufgetreten", Toast.LENGTH_LONG).show()
        })

        queue.add(request)
    }

    private fun sendGet(path: String) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val url = "http://192.168.137.1:4242/$path" // TODO: No hardcoded IP
        val queue = Volley.newRequestQueue(this)

        val stringReq = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->

                var stringResponse = response.toString()
                val jsonArray = JSONArray(stringResponse)
                for (i in 0 until jsonArray.length()) {
                    var jsonInner: JSONObject = jsonArray.getJSONObject(i)
                    val description = jsonInner.get("description") as String
                    val responseLat = jsonInner.get("lat") as Double
                    val responseLong = jsonInner.get("long") as Double
                    mMap.addMarker(MarkerOptions().position(LatLng(responseLat, responseLong)).title(description))
                }
            },
            Response.ErrorListener { Log.e("HTTP", "Error in resolving!") })
        queue.add(stringReq)
    }

    private fun displayAlert(latLng: LatLng) {
        val alert = AlertDialog.Builder(this)
        var editTextData: EditText? = null

        // Builder
        with(alert) {
            setTitle("Hindernis Eingabe")
            setMessage("Gebe den Namen des Hindernisses ein!")

            // Add any  input field here
            editTextData = EditText(context)
            editTextData!!.hint = "z.B. 'Eine Treppe, über die man stolpern kann'"
            editTextData!!.inputType = InputType.TYPE_CLASS_TEXT

            setPositiveButton("Hinzufügen") { dialog, whichButton ->
                val jsonBody =
                    JSONObject("{\"id\": ${(0..99999999).random()}, \"description\": \"${editTextData!!.text}\", \"lat\": ${latLng.latitude}, \"long\": ${latLng.longitude}}")
                sendPost("obstacles", jsonBody)
                dialog.dismiss()
            }

            setNegativeButton("Abbrechen") { dialog, whichButton ->
                dialog.dismiss()
            }
        }

        // Dialog
        val dialog = alert.create()
        dialog.setView(editTextData)
        dialog.show()
    }

    private fun IntRange.random() =
        Random().nextInt((endInclusive + 1) - start) + start
}
