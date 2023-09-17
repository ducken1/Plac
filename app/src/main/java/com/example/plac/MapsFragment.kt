package com.example.plac

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.plac.databinding.FragmentHomeBinding
import com.example.plac.databinding.FragmentMapsBinding

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    private lateinit var binding: FragmentMapsBinding


    private val callback = OnMapReadyCallback { googleMap ->

        val styleOptions = MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.custom_map_style)
        googleMap.setMapStyle(styleOptions)

        val slovenia = LatLng(46.1512, 14.9955)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(slovenia, 7.4f))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsBinding.inflate(inflater, container, false)


//        binding.btMap2.setOnClickListener {
//          //  findNavController().navigate(R.id.action_mapsFragment_to_homeFragment)
//        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}