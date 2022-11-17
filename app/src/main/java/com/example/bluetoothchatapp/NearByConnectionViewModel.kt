package com.example.bluetoothchatapp

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*


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
            payload.asBytes()?.let {
                opponentMessage = it.decodeToString().toString()
            }
            val (senderName, message) = extractSenderNameAndMessage(opponentMessage!!)

            if (isImHost) {
                sendData(senderName, message)
            }

            if (senderName != userName) {
                val obj = Data(1, senderName, message)
                _msgList.value = (_msgList.value?.plus(obj)
                    ?: mutableListOf(obj)) as MutableList<Data>?
            }
        }

        override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) {
        }
    }

    fun sendData(messageSenderName: String, message: String) {
        val messageWithName = addNameOnMessage(messageSenderName, message)
        connectedEndPointList.value?.let {
            connectionsClient.sendPayload(
                it, Payload.fromBytes(messageWithName.toByteArray())
            )
        }
        if (userName == messageSenderName) {
            val obj = Data(0, messageSenderName, message)
            _msgList.value = (_msgList.value?.plus(obj)
                ?: mutableListOf(obj)) as MutableList<Data>?
        }

    }

    private fun addNameOnMessage(name: String, message: String): String {
        return "$message$divider$name"
    }

    private fun extractSenderNameAndMessage(opponentMessage: String): Pair<String, String> {
        var right = opponentMessage.length - 1
        var left = right - 5

        var actualMessage: String = ""
        var senderName: String = ""
        while (left > 0) {
            if (opponentMessage.subSequence(left, right + 1) == divider) {
                actualMessage = opponentMessage.subSequence(0, left).toString()
                senderName =
                    opponentMessage.subSequence(right + 1, opponentMessage.length).toString()
                break
            }
            right--
            left--
        }
        return Pair(senderName, actualMessage)
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