package com.eighteam.ojek.ui.auth.signUp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import com.eighteam.ojek.R
import com.eighteam.ojek.ui.auth.AuthActivity

class SignUpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSignUp: Button = view.findViewById(R.id.btnSignUp2)
        btnSignUp.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_signUp_success, null)

            (activity as AuthActivity).toolbarSignUpSuccess()
        }
    }

}