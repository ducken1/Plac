package com.example.plac

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.plac.databinding.FragmentOglasBinding

class OglasFragment : Fragment() {

    private lateinit var binding: FragmentOglasBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOglasBinding.inflate(inflater, container, false)



        return binding.root
    }

}