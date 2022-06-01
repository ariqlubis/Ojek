package com.eighteam.ojek

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.eighteam.ojek.databinding.ActivityRequestOrderBinding
import com.eighteam.ojek.model.SelectedPlaceEvent
import com.eighteam.ojek.remote.IGoogleAPI
import com.eighteam.ojek.remote.RetrofitClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.CompositeDisposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class RequestOrderActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityRequestOrderBinding
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var selectedPlaceEvent: SelectedPlaceEvent
    private lateinit var blackPolyLine: Polyline
    private lateinit var greyPolyLine: Polyline
    private lateinit var polyLineOption: PolylineOptions
    private lateinit var blackPolyLineOption: PolylineOptions
    private lateinit var polyLineList: ArrayList<LatLng>
    private lateinit var pickUpMarker: Marker
    private lateinit var destinationMarker: Marker
    private lateinit var iGoogleAPI: IGoogleAPI
    private val compositeDisposable = CompositeDisposable()


    override fun onStart() {
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
        super.onStart()
    }

    override fun onStop() {
        compositeDisposable.clear()
        if(EventBus.getDefault().hasSubscriberForEvent(SelectedPlaceEvent::class.java))
            EventBus.getDefault().removeStickyEvent(SelectedPlaceEvent::class.java)
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onSelectedPlaceEvent(event: SelectedPlaceEvent) {
        selectedPlaceEvent = event
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRequestOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun init() {
        iGoogleAPI = RetrofitClient.instance!!.create(IGoogleAPI::class.java)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.setOnMyLocationClickListener {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedPlaceEvent.pickUp, 18f))
        }

        val locationButton = (mapFragment.requireView()
            .findViewById<View>("1".toInt()).parent as View)
            .findViewById<View>("2".toInt())

        val params = locationButton.layoutParams as RelativeLayout.LayoutParams
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        params.addRule(RelativeLayout.ALIGN_PARENT_END, 0)
        params.marginStart = 15
        params.bottomMargin = 250

        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this,
                    R.raw.uber_maps_style
                )
            )
            if (!success) {
                Snackbar.make(
                    mapFragment.requireView(),
                    "load map style is failed",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            Snackbar.make(mapFragment.requireView(), e.message.toString(), Snackbar.LENGTH_LONG).show()
        }

    }
}