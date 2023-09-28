package com.example.plac

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plac.databinding.FragmentHomeBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private var mapsFragment: MapsFragment? = null
    private lateinit var databaseReference: DatabaseReference

    private lateinit var navView: NavigationView

    private lateinit var adapter: FirebaseRecyclerAdapter<OglasData, RvOglasiAdapter.ViewHolder>

    private var isFunctionExecuted = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        databaseReference = FirebaseDatabase.getInstance().getReference("Oglasi")

        allData()

        binding.rvOglasi.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this.context)
        }

        binding.searchViewLayout.setOnClickListener {
            binding.searchView.isIconified = false
            searchData()
        }

        navView = requireActivity().findViewById(R.id.nav_view2)

        navView.findViewById<Button>(R.id.buttonForOglasiId).setOnClickListener {
            if (isFunctionExecuted) {
                allDataAgain()
            } else {
                filterData()
            }
            isFunctionExecuted = !isFunctionExecuted
        }

        binding.btMap.setOnClickListener {
            if (mapsFragment == null) {
                mapsFragment = MapsFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, mapsFragment!!)
                    .commit()

            } else if (mapsFragment!!.isAdded) {
                if (mapsFragment!!.isVisible) {
                    parentFragmentManager.beginTransaction()
                        .hide(mapsFragment!!)
                        .commit()
                } else {
                    parentFragmentManager.beginTransaction()
                        .show(mapsFragment!!)
                        .commit()
                }
            }
        }
        return binding.root
    }

    private fun filterData() {

        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        if (userId != null) {
            val options = FirebaseRecyclerOptions.Builder<OglasData>()
                .setQuery(
                    databaseReference
                        .orderByChild("userId")
                        .equalTo(userId),
                    OglasData::class.java
                )
                .build()

            adapter.updateOptions(options)
            binding.rvOglasi.adapter = adapter
        }

    }

    private fun searchData() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val query = newText?.lowercase(Locale.getDefault())
                val filteredOptions = FirebaseRecyclerOptions.Builder<OglasData>()
                    .setQuery(
                        databaseReference
                            .orderByChild("name")
                            .startAt(query)
                            .endAt(query + "\uf8ff"),
                        OglasData::class.java
                    )
                    .build()
                adapter.updateOptions(filteredOptions)
                binding.rvOglasi.adapter = adapter

                return true
            }
        })
    }
    private fun allDataAgain() {

        val options = FirebaseRecyclerOptions.Builder<OglasData>()
            .setQuery(databaseReference, OglasData::class.java)
            .build()

        adapter.updateOptions(options)
        binding.rvOglasi.adapter = adapter

    }

    private fun allData() {

        val options = FirebaseRecyclerOptions.Builder<OglasData>()
            .setQuery(databaseReference, OglasData::class.java)
            .build()

        adapter = RvOglasiAdapter(options)
        binding.rvOglasi.adapter = adapter
    }


    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

}