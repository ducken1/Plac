package com.example.plac

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.renderscript.Sampler.Value
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.example.plac.databinding.ActivityMainBinding
import com.example.plac.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var  databaseReference: DatabaseReference

    private var loginSuccessListener: OnLoginSuccessListener? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("Users")

        binding.loginbtn.setOnClickListener {
            val mail = binding.usernameLogin.text.toString()
            val password = binding.passwordLogin.text.toString()

            if (mail.isNotEmpty() && password.isNotEmpty()) {
                login(mail, password)
            }
            else {
                Toast.makeText(requireContext(), "Empty fields", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnLoginSuccessListener) {
            loginSuccessListener = context
        } else {
            throw RuntimeException("$context must implement OnLoginSuccessListener")
        }
    }
    private fun login(mail: String, password: String) {
        databaseReference.orderByChild("mail").equalTo(mail).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (userSnapshot in dataSnapshot.children) {
                        val userData = userSnapshot.getValue(UserData::class.java)
                        firebaseAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener {
                                Toast.makeText(requireContext(), "Login Successful", Toast.LENGTH_SHORT).show()
                                val userName = userData?.name
                                loginSuccessListener?.onLoginSuccess(userName)
                                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                            }
                    }
                } else {
                    Toast.makeText(requireContext(), "Login Failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    interface OnLoginSuccessListener {
        fun onLoginSuccess(userName: String?)

    }

}