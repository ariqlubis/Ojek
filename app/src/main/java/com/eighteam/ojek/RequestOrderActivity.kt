package com.eighteam.ojek

import android.Manifest
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.eighteam.ojek.BuildConfig.MAPS_API_KEY
import com.eighteam.ojek.common.Common
import com.eighteam.ojek.model.SelectedPlaceEvent
import com.eighteam.ojek.remote.IGoogleAPI
import com.eighteam.ojek.remote.RetrofitClient
import com.eighteam.ojek.ui.home.HomeFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.PolyUtil
import com.google.maps.android.ui.IconGenerator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject


class RequestOrderActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var selectedPlaceEvent: SelectedPlaceEvent

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // Routes
    private val compositeDisposable = CompositeDisposable()
    private lateinit var iGoogleAPI: IGoogleAPI
    private var blackPolyLine: Polyline?= null
    private var greyPolyLine: Polyline?= null
    private var polyLineOption: PolylineOptions?= null
    private var blackPolyLineOption: PolylineOptions?= null
    private var polyLineList: ArrayList<LatLng>?= null
    private var originMarker: Marker?= null
    private var destinationMarker: Marker?=null

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
        setContentView(R.layout.activity_request_order)

        init()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun init() {
        iGoogleAPI = RetrofitClient.instance!!.create(IGoogleAPI::class.java)
    }

    @SuppressLint("MissingPermission")
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
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                HomeFragment.LOCATION_PERMISSION_REQUEST
            )
            return
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = true

            mMap.setOnMyLocationClickListener {

                fusedLocationProviderClient.lastLocation

                    .addOnFailureListener {
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
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

            try {
                val success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        this,
                        R.raw.uber_maps_style
                    )
                )
                if (!success)
                    Snackbar.make(mapFragment.requireView(), "Load map style failed", Snackbar.LENGTH_LONG).show()
            } catch (e: Exception) {
                Snackbar.make(mapFragment.requireView(), e.message!!, Snackbar.LENGTH_LONG).show()
            }

        }

        if(ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        drawPath(selectedPlaceEvent)

        val locationButton = (findViewById<View>("1".toInt()).parent as View)
            .findViewById<View>("2".toInt())

        val params = locationButton.layoutParams as RelativeLayout.LayoutParams
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        params.addRule(RelativeLayout.ALIGN_PARENT_END, 0)
        params.marginStart = 15
        params.bottomMargin = 250



    }

    private fun drawPath(selectedPlaceEvent: SelectedPlaceEvent) {

        compositeDisposable.add(
            iGoogleAPI.getDirections(
                "driving",
                "less_driving",
                selectedPlaceEvent.originString, selectedPlaceEvent.destinationString, MAPS_API_KEY
            ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { returnResult ->
                Log.d("API_RETURN", returnResult)
                try {
                    val jsonObject = JSONObject(returnResult)
                    val jsonArray = jsonObject.getJSONArray("routes")
                    for (i in 0 until jsonArray.length())
                    {
                        val route = jsonArray.getJSONObject(i)
                        val poly = route.getJSONObject("overview_polyline")
                        val polyline = poly.getString("points")

                        polyLineList = PolyUtil.decode(polyline) as ArrayList<LatLng>?
                    }

                    polyLineOption = PolylineOptions()
                    polyLineOption!!.color(Color.GRAY)
                    polyLineOption!!.width(12f)
                    polyLineOption!!.startCap(SquareCap())
                    polyLineOption!!.jointType(JointType.ROUND)
                    polyLineOption!!.addAll(polyLineList!!)
                    greyPolyLine = mMap.addPolyline(polyLineOption!!)

                    blackPolyLineOption = PolylineOptions()
                    blackPolyLineOption!!.color(Color.BLACK)
                    blackPolyLineOption!!.width(5f)
                    blackPolyLineOption!!.startCap(SquareCap())
                    blackPolyLineOption!!.jointType(JointType.ROUND)
                    blackPolyLineOption!!.addAll(polyLineList!!)
                    blackPolyLine = mMap.addPolyline(blackPolyLineOption!!)

                    // animator
                    val valueAnimator = ValueAnimator.ofInt(0,100)
                    valueAnimator.duration = 1100
                    valueAnimator.repeatCount = ValueAnimator.INFINITE
                    valueAnimator.interpolator = LinearInterpolator()
                    valueAnimator.addUpdateListener { value ->
                        val points = greyPolyLine!!.points
                        val percentValue = value.animatedValue.toString().toInt()
                        val size = points.size
                        val newPoints = (size * (percentValue/100.0f)).toInt()
                        val p = points.subList(0, newPoints)
                        blackPolyLine!!.points = p
                    }
                    valueAnimator.start()

                    val latLngBound = LatLngBounds.Builder().include(selectedPlaceEvent.origin)
                        .include(selectedPlaceEvent.destination).build()

                    val objects = jsonArray.getJSONObject(0)
                    val legs = objects.getJSONArray("legs")
                    val legsObject = legs.getJSONObject(0)
                    val time = legsObject.getJSONObject("duration")
                    val duration = time.getString("text")

                    val start_address = legsObject.getString("start_address")
                    val end_address = legsObject.getString("end_address")

                    addOriginMarker(duration, start_address)
                    addDestinationMarker(duration, end_address)

                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBound, 160))
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(mMap.cameraPosition.zoom-1))


                } catch (e: java.lang.Exception) {
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
                    
            })
    }

    private fun addDestinationMarker(duration: String, endAddress: String) {
        val view = layoutInflater.inflate(R.layout.origin_info_windows, null)

        val txt_destination = view.findViewById<View>(R.id.tv_destination) as TextView
        txt_destination.text = Common.formatAddress(endAddress)

        val generator = IconGenerator(this)
        generator.setContentView(view)
        generator.setBackground(ColorDrawable(Color.TRANSPARENT))
        val icon = generator.makeIcon()

        destinationMarker = mMap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(icon))
            .position(selectedPlaceEvent.origin))


    }

    private fun addOriginMarker(duration: String, startAddress: String) {
        val view = layoutInflater.inflate(R.layout.destination_info_windows, null)

        val txt_time = view.findViewById<View>(R.id.tv_time) as TextView
        val txt_origin = view.findViewById<View>(R.id.tv_origin) as TextView

        txt_time.text = Common.formatDuration(duration)
        txt_origin.text = Common.formatAddress(startAddress)

        val generator = IconGenerator(this)
        generator.setContentView(view)
        generator.setBackground(ColorDrawable(Color.TRANSPARENT))
        val icon = generator.makeIcon()

        originMarker = mMap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(icon))
            .position(selectedPlaceEvent.origin))

    }

}