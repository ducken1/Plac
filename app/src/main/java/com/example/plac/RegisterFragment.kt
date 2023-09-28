package com.example.plac

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.plac.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("Users")


        binding.registerbtn.setOnClickListener {
            val name = binding.name.text.toString()
            val mail = binding.mail.text.toString()
            val password = binding.passwordRegister.text.toString()
            val confirmPassword = binding.confirmpasswordRegister.text.toString()

            val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}".toRegex()

            if (name.isEmpty() || mail.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Empty fields", Toast.LENGTH_SHORT).show()
            } else if (!mail.matches(emailPattern)) {
                Toast.makeText(requireContext(), "Invalid email address format", Toast.LENGTH_SHORT).show()
            } else if (password.length < 6) {
                Toast.makeText(requireContext(), "Password must be longer than 6 characters", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                // All checks passed, proceed to registration
                register(name, mail, password)
            }


        }

        return view
    }
    private fun register(name: String, mail: String, password: String) {
        databaseReference.orderByChild("mail").equalTo(mail).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    firebaseAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener {
                        //val id = databaseReference.push().key
                        val user = firebaseAuth.currentUser
                        val userId = user?.uid
                        val userData = UserData(userId, name, mail, password)
                        databaseReference.child(userId!!).setValue(userData)
                        Toast.makeText(requireContext(), "Register successful", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                    }
                } else {
                    Toast.makeText(requireContext(), "User already exists", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}