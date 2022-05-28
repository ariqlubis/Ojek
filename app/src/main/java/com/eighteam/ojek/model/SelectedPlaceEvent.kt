package com.eighteam.ojek.model

import com.google.android.gms.maps.model.LatLng

data class SelectedPlaceEvent(var pickUp: LatLng, var destination: LatLng) {
    val iPickUp: String
        get() =
            StringBuilder()
                .append(pickUp.latitude)
                .append(",")
                .append(pickUp.longitude)
                .toString()


    val iDestination: String
        get() =
            StringBuilder()
                .append(destination.latitude)
                .append(",")
                .append(pickUp.longitude)
                .toString()
}