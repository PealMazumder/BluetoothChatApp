package com.example.bluetoothchatapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.bluetoothchatapp.databinding.FragmentCreateBinding

class CreateFragment : Fragment() {
    private val viewModel: NearByConnectionViewModel by activityViewModels()
    private lateinit var binding: FragmentCreateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isImHost = true
        binding.btnStart.setOnClickListener {
            viewModel.groupName = binding.etGroupName.text.toString()
            viewModel.userName = binding.etName.text.toString()
            viewModel.startAdvertising(requireActivity().packageName)
            val action = CreateFragmentDirections.actionCreateFragmentToChatFragment()
            findNavController().navigate(action)
        }

    }
}