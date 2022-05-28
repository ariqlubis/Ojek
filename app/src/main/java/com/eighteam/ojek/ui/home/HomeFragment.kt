package com.eighteam.ojek.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.eighteam.ojek.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.snackbar.Snackbar

@Suppress("DEPRECATION")
class HomeFragment : Fragment(), OnMapReadyCallback {

    companion object {
        const val LOCATION_PERMESSION_REQUEST = 12
    }

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mMap: GoogleMap

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    //load drivers
    private var distance = 1.0
    private val LIMIT_RANGE = 10.0
    private var previousLocation: Location? = null
    private var currentLocation: Location? = null

    var firstTime = true

    var cityName = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]
        val root = inflater.inflate(R.layout.fragment_home, container, false)


        init()

        mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return root


    }

    @SuppressLint("MissingPermission")
    private fun init() {
        //location
        locationRequest = LocationRequest()
        locationRequest.apply {
            this.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            this.fastestInterval = 3000
            this.interval = 5000
            this.smallestDisplacement = 10f
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                val newPos = LatLng(
                    p0.lastLocation.latitude,
                    p0.lastLocation.longitude
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPos, 18f))

                //if user has change location, load the drivers again
                if (firstTime) {
                    previousLocation = p0.lastLocation
                    currentLocation = p0.lastLocation

                    firstTime = false
                } else {
                    previousLocation = currentLocation
                    currentLocation = p0.lastLocation
                }

                if (previousLocation!!.distanceTo(currentLocation) / 1000 <= LIMIT_RANGE) {
                    loadAvailableDrivers()
                }


            }
        }

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMESSION_REQUEST
            )
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback,
            Looper.myLooper()!!
        )

        loadAvailableDrivers()


    }

    private fun loadAvailableDrivers() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }


    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        mMap = p0

        mMap.uiSettings.isZoomControlsEnabled = true

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMESSION_REQUEST
            )
            return
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = true

            mMap.setOnMyLocationButtonClickListener {

                fusedLocationProviderClient.lastLocation

                    .addOnFailureListener {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }

                    .addOnSuccessListener { location ->
                        val newPos = LatLng(location.latitude, location.longitude)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPos, 18f))
                    }


                true
            }
            val locationButton = (mapFragment.requireView()
                .findViewById<View>("1".toInt()).parent!! as View).findViewById<View>("2".toInt())
            val params = locationButton.layoutParams as RelativeLayout.LayoutParams
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            params.addRule(RelativeLayout.ALIGN_PARENT_END, 0)
            params.marginStart = 15
            params.bottomMargin = 250

            //set map style
            try {
                val success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireContext(),
                        R.raw.uber_maps_style
                    )
                )
                if (!success) {
                    Snackbar.make(
                        requireView(),
                        "load map style is failed",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Snackbar.make(requireView(), e.message.toString(), Snackbar.LENGTH_LONG).show()
            }
        }

    }
}