package com.eighteam.ojek.ui.auth.signIn

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.eighteam.ojek.R
import com.eighteam.ojek.ui.auth.AuthActivity

class SignInFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnSignUp: Button = view.findViewById(R.id.btnSignUp)
        btnSignUp.setOnClickListener {
            val signUp = Intent(activity, AuthActivity::class.java)
            signUp.putExtra(AuthActivity.EXTRA_PAGE_REQUEST, 1)
            startActivity(signUp)
        }
    }


}