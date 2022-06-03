package com.eighteam.ojek.model

import com.google.android.gms.maps.model.LatLng

data class SelectedPlaceEvent(var origin: LatLng, var destination: LatLng) {
    val originString: String
        get() =
            StringBuilder()
                .append(origin.latitude)
                .append(",")
                .append(origin.longitude)
                .toString()


    val destinationString: String
        get() =
            StringBuilder()
                .append(destination.latitude)
                .append(",")
                .append(origin.longitude)
                .toString()
}