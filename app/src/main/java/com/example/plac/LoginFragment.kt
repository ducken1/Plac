package com.example.plac

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.example.plac.databinding.ActivityMainBinding
import com.example.plac.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root


        firebaseAuth = FirebaseAuth.getInstance()

        binding.loginbtn.setOnClickListener {
            val mail = binding.usernameLogin.text.toString()
            val password = binding.passwordLogin.text.toString()

            if (mail.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        //premaknes se med fragmenti ki so povezani v main_graph
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    }
                    else {
                        Toast.makeText(requireContext(), "Wrong username or password", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else {
                Toast.makeText(requireContext(), "Fields empty!", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}