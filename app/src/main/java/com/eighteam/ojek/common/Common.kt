package com.eighteam.ojek.common

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.eighteam.ojek.R
import com.eighteam.ojek.model.UserModel

object Common {
    val TOKEN_REFERENCE: String = "Token"
    var currentUser: UserModel? = null
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