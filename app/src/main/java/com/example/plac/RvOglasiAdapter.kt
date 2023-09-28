package com.example.plac

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.plac.databinding.RvOglasiItemBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

import com.squareup.picasso.Picasso

class RvOglasiAdapter(options: FirebaseRecyclerOptions<OglasData>) :
    FirebaseRecyclerAdapter<OglasData, RvOglasiAdapter.ViewHolder>(options) {

    class ViewHolder(val binding: RvOglasiItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            RvOglasiItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

//    override fun getItemCount(): Int {
//        return oglasList.size
//    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: OglasData) {

        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

//        val currentItem = oglasList[position]
        val currentItem = getItem(position)
        holder.apply {
            binding.apply {
                tvNameItem.text = currentItem.name
                Picasso.get().load(currentItem.firstImage).into(imgItem)

                if (userId == currentItem.userId) {
                    btnEdit.visibility = View.VISIBLE
                    btnRemove.visibility = View.VISIBLE
                } else {
                    btnEdit.visibility = View.GONE
                    btnRemove.visibility = View.GONE
                }

                btnEdit.setOnClickListener {
                    val action = HomeFragmentDirections.actionHomeFragmentToUpdateOglasFragment(
                        currentItem.oglasId.toString(),
                        currentItem.userId.toString(),
                        currentItem.name.toString(),
                        currentItem.firstImage.toString()
                    )
                    findNavController(holder.itemView).navigate(action)
                }

                btnRemove.setOnClickListener {
                    MaterialAlertDialogBuilder(holder.itemView.context)
                        .setTitle("Delete item permanently")
                        .setMessage("Are you sure you want to delete this item?")
                        .setPositiveButton("Yes") { _, _ ->

                            val firebaseRef = FirebaseDatabase.getInstance().getReference("Oglasi")
                            val storageRef = FirebaseStorage.getInstance().getReference("Images")

                            storageRef.child(currentItem.oglasId.toString()).delete()

                            firebaseRef.child(currentItem.oglasId.toString()).removeValue()
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        holder.itemView.context,
                                        "Item removed successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .addOnFailureListener { error ->
                                    Toast.makeText(
                                        holder.itemView.context,
                                        "error ${error.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                        .setNegativeButton("No") { _, _ ->
                            Toast.makeText(holder.itemView.context, "canceled", Toast.LENGTH_SHORT)
                                .show()
                        }
                        .show()
                }

            }
        }
    }
}