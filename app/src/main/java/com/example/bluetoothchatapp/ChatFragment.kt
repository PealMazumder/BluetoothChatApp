package com.example.bluetoothchatapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bluetoothchatapp.databinding.FragmentChatBinding

class ChatFragment : Fragment() {
    private val viewModel: NearByConnectionViewModel by activityViewModels()
    private lateinit var binding: FragmentChatBinding
    lateinit var messageAdapter: MessageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        messageAdapter = MessageAdapter()
        binding.chatList.adapter = messageAdapter
        binding.chatList.layoutManager = LinearLayoutManager(requireContext())
        binding.chatList.setHasFixedSize(true)

        binding.sendBtn.setOnClickListener {
            sendMessage()
        }
        binding.sendMsgText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
            }
            false

        })

        viewModel.msgList.observe(this.viewLifecycleOwner) {
            messageAdapter.submitList(it)
            messageAdapter.notifyDataSetChanged()
            binding.chatList.scrollToPosition(viewModel.msgList.value?.size?.minus(1) ?: 0)

        }
    }

    private fun sendMessage() {
        viewModel.sendData(viewModel.userName, binding.sendMsgText.text.toString())
        binding.sendMsgText.text?.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.connectionsClient.stopAdvertising()
        viewModel.connectionsClient.stopDiscovery()
        resetInfo()
    }

    private fun resetInfo() {
        viewModel.apply {
            groupEndPointId.clear()
            userName = ""
            groupName = ""
            opponentMessage = ""
        }
    }
}