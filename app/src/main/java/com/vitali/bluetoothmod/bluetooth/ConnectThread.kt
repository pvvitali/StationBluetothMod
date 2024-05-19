package com.vitali.bluetoothmod.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import okio.IOException
import java.util.UUID

class ConnectThread(
    private val device: BluetoothDevice,
    private val listener: BluetoothController.Listener
) : Thread() {
    private val uuid = "00001101-0000-1000-8000-00805F9B34FB"
    private var mSocket: BluetoothSocket? = null

    init {
        try {
            mSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid))
        } catch (e: IOException) {

        } catch (e: SecurityException) {

        }
    }

    override fun run() {
        super.run()

        try {
            //Log.i("MyMess", "Connecting...")
            mSocket?.connect()
            listener.onReceive(BluetoothController.BLUETOOTH_CONNECTED)
            readMessage()
            //Log.i("MyMess", "Connected Ok!")
        } catch (e: IOException) {
            listener.onReceive(BluetoothController.BLUETOOTH_NO_CONNECTED)
            //Log.i("MyMess", "Error connection!")
        } catch (e: SecurityException) {

        }
    }

    private fun readMessage() {
        val buffer = ByteArray(256)
        while (true) {
            try {
                val length = mSocket?.inputStream?.read(buffer)
                val message = String(buffer, 0, length ?: 0)
                listener.onReceive(message)
            } catch (e: IOException) {
                listener.onReceive(BluetoothController.BLUETOOTH_NO_CONNECTED)
                break
            }
        }

    }

    fun sendMessage(message: String) {
        try {
            mSocket?.outputStream?.write(message.toByteArray())
        } catch (e: IOException) {

        }
    }

    fun closeConnection() {

        try {
            mSocket?.close()
        } catch (e: IOException) {

        }
    }
}