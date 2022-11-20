package com.example.bluetoothchatapp

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import org.apache.commons.lang3.SerializationUtils


class NearByConnectionViewModel : ViewModel() {
    lateinit var connectionsClient: ConnectionsClient
    private val STRATEGY = Strategy.P2P_CLUSTER
    private val divider = "[#?@%]"
    val groupEndPointId = hashMapOf<String, String>()
    var isImHost = false

    private val _connectedEndPointList = MutableLiveData<MutableList<String>>()
    private val connectedEndPointList: LiveData<MutableList<String>> = _connectedEndPointList

    private val _msgList = MutableLiveData<MutableList<Data>>()
    val msgList: LiveData<MutableList<Data>> = _msgList

    private val _groupsName = MutableLiveData<MutableList<String>>()
    val groupsName: LiveData<MutableList<String>> = _groupsName

    var userName = ""
    var groupName = ""

    var opponentMessage = ""

    fun initConnectionsClient(context: Context) {
        connectionsClient = Nearby.getConnectionsClient(context)
    }


    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            connectionsClient.acceptConnection(endpointId, payloadCallback)
            _connectedEndPointList.value = (_connectedEndPointList.value?.plus(endpointId)
                ?: mutableListOf(endpointId)) as MutableList<String>?
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if (result.status.isSuccess) {
                connectionsClient.stopDiscovery()
            }
        }

        override fun onDisconnected(endpointId: String) {
        }
    }


    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            groupEndPointId[info.endpointName] = endpointId
            _groupsName.value = (_groupsName.value?.plus(info.endpointName)
                ?: mutableListOf(info.endpointName)) as MutableList<String>?
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
            var messageInfo = MessageInfo()
            payload.asBytes()?.let {
                messageInfo = SerializationUtils.deserialize(it)
            }

            if (isImHost) {
                sendData(messageInfo)
            }

            if (messageInfo.name != userName) {
                val obj = Data(1, messageInfo.name, messageInfo.message)
                _msgList.value = (_msgList.value?.plus(obj)
                    ?: mutableListOf(obj)) as MutableList<Data>?
            }
        }

        override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) {
        }
    }

    fun sendData(messageInfo: MessageInfo) {
        val data = SerializationUtils.serialize(messageInfo)
        connectedEndPointList.value?.let {
            connectionsClient.sendPayload(
                it, Payload.fromBytes(data)
            )
        }
        if (userName == messageInfo.name) {
            val obj = Data(0, messageInfo.name, messageInfo.message)
            _msgList.value = (_msgList.value?.plus(obj)
                ?: mutableListOf(obj)) as MutableList<Data>?
        }

    }

    fun startAdvertising(serviceId: String) {
        println("Start Advertising")
        val options = AdvertisingOptions.Builder().setStrategy(STRATEGY).build()
        connectionsClient.startAdvertising(
            groupName, serviceId, connectionLifecycleCallback, options
        )
    }

    fun startDiscovery(serviceId: String) {
        val options = DiscoveryOptions.Builder().setStrategy(STRATEGY).build()
        println("Start Discovery")
        connectionsClient.startDiscovery(serviceId, endpointDiscoveryCallback, options)
    }


}