package com.example.bluetoothchatapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.nearby.connection.*


class NearByConnectionViewModel : ViewModel() {
    lateinit var connectionsClient: ConnectionsClient
    private val STRATEGY = Strategy.P2P_CLUSTER
    val groupEndPointId = hashMapOf<String, String>()

    private val _connectedEndPointList = MutableLiveData<MutableList<String>>()
    val connectedEndPointList: LiveData<MutableList<String>> = _connectedEndPointList

    private val _groupsName = MutableLiveData<MutableList<String>>()
    val groupsName: LiveData<MutableList<String>> = _groupsName


    var userName = ""
    var groupName = ""

    var opponentMessage = ""

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            connectionsClient.acceptConnection(endpointId, payloadCallback)
            connectedEndPointList.value?.add(info.endpointName)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if (result.status.isSuccess) {
                connectionsClient.stopAdvertising()
                connectionsClient.stopDiscovery()
            }
        }

        override fun onDisconnected(endpointId: String) {
        }
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            groupEndPointId[endpointId] = info.endpointName
            _groupsName.value?.add(info.endpointName)
        }

        override fun onEndpointLost(endpiontId: String) {
        }
    }

    fun makeConnection(endpointId: String) {
        connectionsClient.requestConnection(
            userName, endpointId, connectionLifecycleCallback
        )
    }


    private val payloadCallback: PayloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            payload.asBytes()?.let {
                opponentMessage = it.decodeToString().toString()
            }
        }

        override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) {
        }
    }

    fun startAdvertising(serviceId: String) {
        val options = AdvertisingOptions.Builder().setStrategy(STRATEGY).build()
        connectionsClient.startAdvertising(
            userName, serviceId, connectionLifecycleCallback, options
        )
    }

    fun startDiscovery(serviceId: String) {
        val options = DiscoveryOptions.Builder().setStrategy(STRATEGY).build()
        connectionsClient.startDiscovery(serviceId, endpointDiscoveryCallback, options)
    }


}