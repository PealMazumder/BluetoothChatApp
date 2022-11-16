package com.example.bluetoothchatapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.bluetoothchatapp.databinding.FragmentCreateBinding
import com.example.bluetoothchatapp.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnJoinGroup.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToJoinFragment()
            findNavController().navigate(action)
        }

        binding.btnCreateGroup.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToCreateFragment()
            findNavController().navigate(action)
        }
    }
}