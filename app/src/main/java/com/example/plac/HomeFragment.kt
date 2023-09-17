package com.example.plac

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.plac.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private var mapsFragment: MapsFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.btMap.setOnClickListener {
//          findNavController().navigate(R.id.action_homeFragment_to_mapsFragment)
//            val transaction = parentFragmentManager.beginTransaction()
//            transaction.replace(R.id.fragment_container, MapsFragment())
//            transaction.commit()
            if (mapsFragment == null) {
                mapsFragment = MapsFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, mapsFragment!!)
                    .commit()

                binding.btnNeke.visibility = View.GONE
            } else if (mapsFragment!!.isAdded) {
                // Toggle the visibility of the MapsFragment
                if (mapsFragment!!.isVisible) {
                    parentFragmentManager.beginTransaction()
                        .hide(mapsFragment!!)
                        .commit()

                    binding.btnNeke.visibility = View.VISIBLE
                } else {
                    parentFragmentManager.beginTransaction()
                        .show(mapsFragment!!)
                        .commit()

                    binding.btnNeke.visibility = View.GONE
                }
            }
        }

        binding.btnNeke.setOnClickListener {
            Toast.makeText(context, "blabla", Toast.LENGTH_SHORT).show()
            Log.d("MyTag", "User ID:")
        }

//        binding.btMap3.setOnClickListener {
//            if (mapsFragment != null) {
//                // Hide the MapsFragment if it's visible
//                parentFragmentManager.beginTransaction()
//                    .hide(mapsFragment!!)
//                    .commit()
//            }
//        }

        return binding.root
    }
}