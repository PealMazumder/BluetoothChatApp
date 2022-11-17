package com.example.bluetoothchatapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bluetoothchatapp.databinding.DeviceListBinding

class DeviceAdapter(private val mCallback: Callback? = null) : ListAdapter<String,
        DeviceAdapter.DeviceListViewHolder>(DiffCallback) {
    class DeviceListViewHolder(var binding: DeviceListBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(name: String) {
            println("Device adapter $name")
            binding.tvGroupName.text = name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceListViewHolder {
        return DeviceListViewHolder(DeviceListBinding.inflate(
            LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: DeviceListViewHolder, position: Int) {
        val name = getItem(position)
        holder.bind(name)
        holder.binding.tvGroupName.setOnClickListener {
            mCallback?.onNameClicked(name)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
    interface Callback {
        fun onNameClicked(groupName: String)
    }

}