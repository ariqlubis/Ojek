package com.eighteam.ojek.ui.auth

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.eighteam.ojek.R

class AuthActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    companion object {
        const val EXTRA_PAGE_REQUEST = "extra_page_request"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val pageRequest = intent.getIntExtra(EXTRA_PAGE_REQUEST, 0)
        if(pageRequest == 1) {
            toolbarSignUp()
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.fragment_signIn, true)
                .build()

            Navigation.findNavController(findViewById(R.id.authHostFragment))
                .navigate(R.id.action_signUp, null, navOptions)
        }
    }

    fun toolbarSignUpSuccess() {
        toolbar.visibility = View.GONE
    }

    fun toolbarSignUp() {
        toolbar = findViewById(R.id.toolbar)
        toolbar.title = "Sign Up"
        toolbar.subtitle = "Register"
        toolbar.navigationIcon= resources.getDrawable(R.drawable.icon_back_arrow, null)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }
}