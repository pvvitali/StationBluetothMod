package com.vitali.bluetoothmod.screens

import android.bluetooth.BluetoothDevice

data class MyBluetoothDevice(
    var device: BluetoothDevice,
    var isChecked: Boolean
)
