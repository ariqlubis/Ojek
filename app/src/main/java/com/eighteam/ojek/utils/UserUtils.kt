package com.eighteam.ojek.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import com.eighteam.ojek.common.Common
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
object UserUtils {
    fun updateUser(
        view: View?,
        updateData: Map<String, Any>
    ) {
        FirebaseDatabase.getInstance()
            .getReference(Common.RIDER_INFO_REFERENCE)
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .updateChildren(updateData)
            .addOnFailureListener { e ->
                Snackbar.make(view!!, e.message!!, Snackbar.LENGTH_LONG).show()
            }.addOnSuccessListener {
                Snackbar.make(view!!, "Update information Success", Snackbar.LENGTH_LONG).show()
            }
    }
}