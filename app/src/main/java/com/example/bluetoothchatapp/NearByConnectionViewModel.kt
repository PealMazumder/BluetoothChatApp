package com.example.bluetoothchatapp

import androidx.lifecycle.ViewModel
import com.google.android.gms.nearby.connection.ConnectionsClient


class NearByConnectionViewModel : ViewModel() {
    lateinit var connectionsClient: ConnectionsClient
}