package com.eighteam.ojek

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.eighteam.ojek.BuildConfig.MAPS_API_KEY
import com.eighteam.ojek.activities.WaitingCancel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import org.json.JSONObject
import com.eighteam.ojek.ui.home.HomeFragment


class RequestOrderActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {

    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_order)
        // Initialize Google Maps and its callbacks
        val mapFragment = supportFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val btnOrder: Button = findViewById(R.id.btn_order)
        btnOrder.setOnClickListener(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        // Sample coordinates
        val latLngOrigin = LatLng(-6.553735220793455, 106.72332279790311) // Ayala
        val latLngDestination = LatLng(-6.562040150289809, 106.72723523209413) // SM City
        this.googleMap!!.addMarker(MarkerOptions().position(latLngOrigin).title("Ayala"))
        this.googleMap!!.addMarker(MarkerOptions().position(latLngDestination).title("SM City"))
        this.googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOrigin, 14.5f))
        val path: MutableList<List<LatLng>> = ArrayList()
        val urlDirections = "https://maps.googleapis.com/maps/api/directions/json?origin=-6.553735220793455, 106.72332279790311 &destination=-6.562040150289809, 106.72723523209413&key=${MAPS_API_KEY}"
        val directionsRequest = object : StringRequest(Method.GET, urlDirections, Response.Listener {
                response ->
            val jsonResponse = JSONObject(response)
            // Get routes
            val routes = jsonResponse.getJSONArray("routes")
            val legs = routes.getJSONObject(0).getJSONArray("legs")
            val steps = legs.getJSONObject(0).getJSONArray("steps")
            for (i in 0 until steps.length()) {
                val points = steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                path.add(PolyUtil.decode(points))
            }
            for (i in 0 until path.size) {
                this.googleMap!!.addPolyline(PolylineOptions().addAll(path[i]).color(Color.RED))
            }
        }, Response.ErrorListener {
        }){}
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(directionsRequest)
    }

    override fun onClick(p0: View?) {
        when(p0?.id) {

            R.id.btn_order -> {
                val moveIntent = Intent(this@RequestOrderActivity, WaitingCancel::class.java)
                startActivity(moveIntent)

            }
        }
    }
}