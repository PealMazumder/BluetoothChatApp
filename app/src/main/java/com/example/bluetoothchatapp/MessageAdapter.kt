package com.example.bluetoothchatapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bluetoothchatapp.databinding.MsgRecieveViewBinding
import com.example.bluetoothchatapp.databinding.MsgSendViewBinding


const val SEND = 0
const val RECEIVE = 1

class MessageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val messages = arrayListOf<Data>()

    fun submitList(data: List<Data>) {
        this.messages.clear()
        this.messages.addAll(data)
    }

    fun addMessage(data: Data) {
        messages.add(data)
        notifyItemInserted(messages.size - 1)
    }


    class SendViewHolder(private val msgSendBinding: MsgSendViewBinding) :
        RecyclerView.ViewHolder(msgSendBinding.root) {
        fun bind(item: Data) = msgSendBinding.apply {
            name.text = item.name
            msgDescription.text = item.message
        }
    }

    class ReceiverViewHolder(private val msgReceiveBinding: MsgRecieveViewBinding) :
        RecyclerView.ViewHolder(msgReceiveBinding.root) {
        fun bind(item: Data) = msgReceiveBinding.apply {
            name.text = item.name
            msgDescription.text = item.message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SEND -> SendViewHolder(
                MsgSendViewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            RECEIVE -> ReceiverViewHolder(
                MsgRecieveViewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw IllegalArgumentException("Invalid ViewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SendViewHolder -> holder.bind(messages[position])
            is ReceiverViewHolder -> holder.bind(messages[position])
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (messages[position].id) {
            0 -> SEND
            1 -> RECEIVE
            else -> throw IllegalArgumentException("Invalid Item")
        }
    }
}