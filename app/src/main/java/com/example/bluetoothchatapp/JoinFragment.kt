package com.example.bluetoothchatapp

import android.app.ActionBar
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bluetoothchatapp.databinding.FragmentJoinBinding
import com.example.bluetoothchatapp.databinding.GroupNameDialogBinding
import com.example.bluetoothchatapp.databinding.UserNameDialogBinding

class JoinFragment : Fragment(), DeviceAdapter.Callback {
    private val viewModel: NearByConnectionViewModel by activityViewModels()
    private lateinit var userNameDialogBinding: UserNameDialogBinding
    private lateinit var binding: FragmentJoinBinding

    private lateinit var deviceAdapter: DeviceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentJoinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showNameDialog()

        viewModel.startDiscovery(requireActivity().packageName)

        binding.tvNearbyDevices.visibility = View.VISIBLE
        binding.rvDeviceList.visibility = View.VISIBLE

        deviceAdapter = DeviceAdapter(this)

        binding.rvDeviceList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDeviceList.adapter = deviceAdapter

        viewModel.groupsName.observe(this.viewLifecycleOwner) {
            deviceAdapter.submitList(it)
        }

    }

    private fun showNameDialog() {
        userNameDialogBinding = UserNameDialogBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext())
        dialog.setContentView(userNameDialogBinding.root)
        dialog.setCancelable(false)
        dialog.show()

        dialog.window?.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)

        userNameDialogBinding.btnSubmit.setOnClickListener {
            val userName = userNameDialogBinding.etName.text.toString()
            viewModel.userName = userName
            dialog.dismiss()
        }
    }


    override fun onNameClicked(groupName: String) {
        viewModel.groupEndPointId[groupName]?.let { viewModel.makeConnection(it) }
    }
}