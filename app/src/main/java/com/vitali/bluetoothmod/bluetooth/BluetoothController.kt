package com.vitali.bluetoothmod.bluetooth

import android.bluetooth.BluetoothDevice

class BluetoothController(val device: BluetoothDevice) {
    private var connectThread: ConnectThread? = null

    fun connect(listener: Listener){
        connectThread = ConnectThread(device, listener)
        connectThread?.start()
    }

    fun sendMessage(message: String) {
        connectThread?.sendMessage(message)
    }

    fun closeConnection() {
        connectThread?.closeConnection()
    }

    companion object{
        const val BLUETOOTH_CONNECTED = "bluetooth_connected"
        const val BLUETOOTH_NO_CONNECTED = "bluetooth_no_connected"
    }

    interface Listener{
        fun onReceive(message: String)
    }
}