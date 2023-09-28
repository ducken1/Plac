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
import androidx.navigation.fragment.navArgs
import com.example.plac.databinding.FragmentUpdateOglasBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso


class UpdateOglasFragment : Fragment() {

    private lateinit var binding: FragmentUpdateOglasBinding
    private val args : UpdateOglasFragmentArgs by navArgs()

    private lateinit var firebaseRef : DatabaseReference
    private lateinit var storageRef : StorageReference

    private var uri: Uri? = null
    private var imageUrl : String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateOglasBinding.inflate(inflater, container, false)

        firebaseRef = FirebaseDatabase.getInstance().getReference("Oglasi")
        storageRef = FirebaseStorage.getInstance().getReference("Images")

        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
            binding.imageViewUpdate.setImageURI(it)
            if (it != null) {
                uri = it
            }
        }

        imageUrl = args.imageUrl

        binding.apply {
            test.setText(args.name)
            Picasso.get().load(imageUrl).into(imageViewUpdate)

            btntest.setOnClickListener {
                updateData()
                findNavController().navigate(R.id.action_updateOglasFragment_to_homeFragment)
            }

            imageViewUpdate.setOnClickListener {
                pickImage.launch("image/*")
//                context?.let { context ->
//                    MaterialAlertDialogBuilder(context)
//                        .setTitle("changing the image")
//                        .setMessage("please select the option")
//                        .setPositiveButton("change image"){_,_->
//
//
//                        }
//                        .setNegativeButton("delete image"){_,_->
//
//                            imageUrl = null
//                            binding.imageViewUpdate.setImageResource(R.drawable.avatar)
//                        }
//                        .setNeutralButton("cancel"){_,_->}
//                        .show()
//                }
            }
        }

        return binding.root
    }

    private fun updateData() {
        val name = binding.test.text.toString()
        var oglasi : OglasData



        if (uri != null){
            storageRef.child(args.oglasId).putFile(uri!!)
                .addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener {url->
                            imageUrl = url.toString()
                            oglasi = OglasData(args.oglasId, args.userId, name, imageUrl)
                            firebaseRef.child(args.oglasId).setValue(oglasi)
                                .addOnCompleteListener{
                                    Toast.makeText(context, " data updated successfully", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener{
                                    Toast.makeText(context, "error ${it.message}", Toast.LENGTH_SHORT).show()
                                }

                        }

                }

        }
        if (uri == null){
            oglasi = OglasData(args.oglasId, args.userId, name, imageUrl)
            firebaseRef.child(args.oglasId).setValue(oglasi)
                .addOnCompleteListener{
                    Toast.makeText(context, " data updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Toast.makeText(context, "error ${it.message}", Toast.LENGTH_SHORT).show()
                }

        }
        if (imageUrl == null) storageRef.child(args.oglasId).delete()
    }

}