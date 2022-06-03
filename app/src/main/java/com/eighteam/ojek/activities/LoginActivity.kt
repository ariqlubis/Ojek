package com.eighteam.ojek.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.eighteam.ojek.R

class LoginActivity : AppCompatActivity() {

    private lateinit var btnSignIn: Button
    private lateinit var btnCreateAcc: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnCreateAcc = findViewById(R.id.btnCreateAcc)
        btnCreateAcc.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}