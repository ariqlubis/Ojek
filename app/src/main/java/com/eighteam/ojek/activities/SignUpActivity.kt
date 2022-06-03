package com.eighteam.ojek.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.eighteam.ojek.R
import com.eighteam.ojek.model.PassengerModel
import com.google.firebase.database.*

class SignUpActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etPhone: EditText

    private lateinit var btnSignUp: Button
    private lateinit var btnLogin: Button

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etPhone = findViewById(R.id.etPhoneNumber)

        databaseReference = FirebaseDatabase.getInstance().reference

        btnSignUp = findViewById(R.id.btnSignUp)
        btnSignUp.setOnClickListener(this)

        btnLogin = findViewById(R.id.btnLogin)
        btnLogin.setOnClickListener(this)
    }

    private fun savePassengerData() {
        // getting values
        val firstName = etFirstName.text.toString().trim()
        val lastName = etLastName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()
        val phone = etPhone.text.toString().trim()

        if(firstName.isEmpty())
            etFirstName.error = "Please enter your first name"
        else if(lastName.isEmpty())
            etLastName.error = "Please enter your first name"
        else if(email.isEmpty())
            etEmail.error = "Please enter your email"
        else if(password.isEmpty())
            etPassword.error = "Please enter your password"
        else if(phone.isEmpty())
            etPhone.error = "Please enter your phone number"
        else {
            val passenger = PassengerModel(firstName, lastName, email)
            databaseReference.child("Passenger").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.hasChild(phone)) {
                        Toast.makeText(this@SignUpActivity, "Phone number is already registered", Toast.LENGTH_LONG).show()
                    } else {
                        databaseReference.child("Passenger").child(phone).setValue(passenger)
                            .addOnCompleteListener {
                                Toast.makeText(this@SignUpActivity, "Register Successfully", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    override fun onClick(p0: View?) {
        when(p0?.id) {
            R.id.btnSignUp -> {
                savePassengerData()
            }
            R.id.btnLogin -> {
                onBackPressed()
            }
        }
    }
}