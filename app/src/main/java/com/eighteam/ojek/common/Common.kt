package com.eighteam.ojek.common

import com.eighteam.ojek.model.PassengerModel

object Common {
    val TOKEN_REFERENCE: String = "Token"
    var currentUser: PassengerModel? = null
    val RIDER_INFO_REFERENCE: String = "RiderInfo"
    val NOTI_BODY: String = "body"
    val NOTI_TITTLE: String = "title"

    fun buildWelcomeMessage(): String {
        return StringBuilder("Welcome, ")
            .append(currentUser!!.firstName)
            .append(" ")
            .append(currentUser!!.lastName)
            .toString()
    }

    fun formatDuration(duration: String): CharSequence? {
        if(duration.contains("mins"))
            return duration.substring(0, duration.length-1)
        else
            return duration
    }

    fun formatAddress(startAddress: String): CharSequence? {
        val firstIndexComma = startAddress.indexOf(",")
        return startAddress.substring(0, firstIndexComma)
    }
}