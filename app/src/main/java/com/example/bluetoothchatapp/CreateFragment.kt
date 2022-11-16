package com.example.bluetoothchatapp

import android.app.ActionBar.LayoutParams
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bluetoothchatapp.databinding.FragmentCreateBinding
import com.example.bluetoothchatapp.databinding.GroupNameDialogBinding

class CreateFragment : Fragment() {
    private val viewModel: NearByConnectionViewModel by activityViewModels()
    private lateinit var deviceAdapter: DeviceAdapter
    private lateinit var binding: FragmentCreateBinding
    private lateinit var groupNameDialogBinding: GroupNameDialogBinding
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

        showNameDialog()

        viewModel.startAdvertising(requireActivity().packageName)

        binding.tvConnectedDevices.visibility = VISIBLE
        binding.rvDeviceList.visibility = VISIBLE
        binding.btnStart.visibility = VISIBLE


        deviceAdapter = DeviceAdapter()

        binding.rvDeviceList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDeviceList.adapter = deviceAdapter

        binding.btnStart.setOnClickListener {
            val action = CreateFragmentDirections.actionCreateFragmentToChatFragment()
            findNavController().navigate(action)
        }

        setUpObservers()
    }

    private fun setUpObservers() {
        viewModel.connectedEndPointList.observe(this.viewLifecycleOwner) {
            deviceAdapter.submitList(it)
        }
    }

    private fun showNameDialog() {
        groupNameDialogBinding = GroupNameDialogBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext())
        dialog.setContentView(groupNameDialogBinding.root)
        dialog.setCancelable(false)
        dialog.show()

        dialog.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        groupNameDialogBinding.btnSubmit.setOnClickListener {
            viewModel.groupName = groupNameDialogBinding.etGroupName.text.toString()
            viewModel.userName = groupNameDialogBinding.etName.text.toString()
            dialog.dismiss()
        }
    }
}