package com.example.plac

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.plac.databinding.FragmentOglasBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class OglasFragment : Fragment() {

    private lateinit var binding: FragmentOglasBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageRef : StorageReference

    private var uri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOglasBinding.inflate(inflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("Oglasi")
        storageRef = FirebaseStorage.getInstance().reference.child("Images")


        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
            binding.imageView.setImageURI(it)
            if (it != null) {
                uri = it
            }
        }

        binding.imageView.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btntest.setOnClickListener {
            val test = binding.test.text.toString()

            if (test.isNotEmpty()) {
                dodajOglas(test)
            } else {
                Toast.makeText(requireContext(), "Fields empty", Toast.LENGTH_SHORT).show()
            }
        }



        return binding.root
    }

    private fun dodajOglas(name: String) {
        val user = firebaseAuth.currentUser
        val userId = user?.uid
        var oglasData: OglasData

        val oglasId = databaseReference.push().key

        if (uri != null) {
            uri?.let {

                storageRef.child(oglasId!!).putFile(it).addOnSuccessListener {task ->
                    task.metadata!!.reference!!.downloadUrl.addOnSuccessListener { url ->
                        val imageUrl = url.toString()


                        oglasData = OglasData(oglasId, userId, name, imageUrl)

                        databaseReference.orderByChild("oglasId").equalTo(oglasId).addListenerForSingleValueEvent(object: ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (!dataSnapshot.exists()) {

                                    databaseReference.child(oglasId).setValue(oglasData)
                                    Toast.makeText(requireContext(), "Oglas dodan successful", Toast.LENGTH_SHORT)
                                        .show()

                                } else {
                                    Toast.makeText(requireContext(), "Name of oglas already exists", Toast.LENGTH_SHORT).show()
                                }
                            }
                            override fun onCancelled(databaseError: DatabaseError) {
                                Toast.makeText(requireContext(), "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                            }

                        })
                    }
                }
            }
            findNavController().navigate(R.id.action_oglasFragment_to_homeFragment)
        } else {
            Toast.makeText(requireContext(), "Please choose an image", Toast.LENGTH_SHORT).show()
        }


    }

}