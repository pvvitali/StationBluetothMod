package com.vitali.bluetoothmod

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.vitali.bluetoothmod.screens.MyBluetoothDevice
import com.vitali.bluetoothmod.screens.ShowBluetooth
import com.vitali.bluetoothmod.ui.theme.BluetoothModTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableFloatStateOf
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vitali.bluetoothmod.bluetooth.BluetoothController
import java.lang.NumberFormatException

class MainActivity() : ComponentActivity(), BluetoothController.Listener{

    val myContext = this
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var btLauncher: ActivityResultLauncher<Intent>
    private lateinit var btPerLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var bluetoothController: BluetoothController
    private var selectedDevice: BluetoothDevice? = null
    private var isDeviceConnected = mutableStateOf(false)
    private var receiverVoltage = mutableFloatStateOf(0.0f)
    private var receiverPwm = mutableFloatStateOf(0.0f)
    private var receiverCurrent = mutableFloatStateOf(0.0f)
    private var receiverPotencial = mutableFloatStateOf(0.0f)
    private var stringReceiver = ""
    private var stringReceiverTemp = ""

    private lateinit var mCoroutineScope: CoroutineScope

    private lateinit var dataStoreManager: DataStoreManager
    private var oldDevice = arrayListOf<Any>("", "", false)

    private var statusBluetooth = mutableStateOf(listOf(false, "Bluetooth выключен", Color.Red))
    private var listDevices: MutableList<MyBluetoothDevice> = mutableStateListOf()
    private var listDiscoveryDevices: MutableList<MyBluetoothDevice> = mutableStateListOf()

    // mutableStateOf for ScreenMain
    private var isSearch = mutableStateOf(false)

    private val mReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                Log.i("MyMess", "BluetoothAdapter.ACTION_STATE_CHANGED")
                when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                    BluetoothAdapter.STATE_OFF -> {
                        statusBluetooth.value = listOf(false, "Bluetooth выключен", Color.Red)
                        listDevices.clear()
                        listDiscoveryDevices.clear()
                    }

                    BluetoothAdapter.STATE_ON -> {
                        statusBluetooth.value = listOf(true, "Bluetooth включен", Color.Blue)
                        getPairedDevices()
                    }
                }

            } else if (intent?.action == BluetoothDevice.ACTION_FOUND) {
                val receiveDevice = intent.getParcelableExtra<BluetoothDevice>(
                    BluetoothDevice.EXTRA_DEVICE,
                    BluetoothDevice::class.java
                )
                if (ActivityCompat.checkSelfPermission(
                        myContext,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                } else {
                    var isInList = false
                    listDiscoveryDevices.forEach {
                        if (it.device.address == receiveDevice?.address) {
                            isInList = true
                        }
                    }
                    if (!isInList && (receiveDevice != null)) listDiscoveryDevices.add(
                        MyBluetoothDevice(receiveDevice, false)
                    )
                    Log.i("MyMess", "Device ${receiveDevice?.name}    ${receiveDevice?.address}")
                }
            } else if (intent?.action == BluetoothDevice.ACTION_BOND_STATE_CHANGED) {
                getPairedDevices()
            } else if (intent?.action == BluetoothAdapter.ACTION_DISCOVERY_FINISHED) {
                isSearch.value = false
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        btLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == ComponentActivity.RESULT_OK) {
                    Toast.makeText(applicationContext, "Bluetooth On", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext, "Bluetooth Off", Toast.LENGTH_LONG).show()
                }
            }

        dataStoreManager = DataStoreManager(this)

        //Init coroutine scope
        mCoroutineScope = CoroutineScope(Dispatchers.IO)
        mCoroutineScope.launch {
            dataStoreManager.getDataStore().collect {
                oldDevice[0] = it[0]
                oldDevice[1] = it[1]
                oldDevice[2] = it[2]
                getPairedDevices()
            }
        }

        mainCheckBtPermission(this)

        setContent {

            val navController = rememberNavController()

            val changeBtStatus = { value: Boolean ->
                if (value) {
                    if (!bluetoothAdapter.isEnabled) {
                        btLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Bluetooth cannot be Off ",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }


            if (bluetoothAdapter?.isEnabled == true) {
                statusBluetooth.value = listOf(true, "Bluetooth включен", Color.Blue)
                //getPairedDevices()
            } else {
                statusBluetooth.value = listOf(false, "Bluetooth выключен", Color.Red)
            }




            BluetoothModTheme {
                NavHost(
                    navController = navController,
                    startDestination = "screen_1",
                ) {
                    composable("screen_1") {
                        ScreenMain(
                            {
                                navController.navigate("screen_2")
                            },
                            device = selectedDevice,
                            ::connectClick,
                            ::disconnectClick,
                            ::sendMessage,
                            isDeviceConnected,
                            receiverVoltage,
                            receiverCurrent,
                            receiverPotencial,
                            receiverPwm
                        )
                    }
                    composable("screen_2") {

                        ShowBluetooth(
                            {
                                //
                                selectedDevice = it.device
                                bluetoothController = BluetoothController(selectedDevice!!)
                                //
                                navController.navigate("screen_1") {
                                    popUpTo("screen_1") {
                                        inclusive = true
                                    }
                                }
                            },
                            myContext,
                            statusBluetooth,
                            isSearch,
                            changeBtStatus,
                            listDevices,
                            listDiscoveryDevices,
                            ::changeChecked,
                            ::searchDevices,
                            ::createBond
                        )
                    }
//                    composable("screen_3"){
//                        Screen3 {
//                            navController.navigate("screen_1"){
//                                popUpTo("screen_1"){
//                                    inclusive = true
//                                }
//                            }
//                        }
//                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i("MyMess", "Start")
        //Init registerReceiver
        registerReceiver(mReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
        registerReceiver(mReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
        registerReceiver(mReceiver, IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED))
        registerReceiver(mReceiver, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
    }

    override fun onStop() {
        super.onStop()
        Log.i("MyMess", "Stop")
        //cancel coroutine scope
        if (::mCoroutineScope.isInitialized && mCoroutineScope.isActive) {
            Log.i("MyMess", "mCoroutineScope.cancel()")
            mCoroutineScope.cancel()
        }
        //unregister receiver
        unregisterReceiver(mReceiver)
    }

    private fun getPairedDevices() {

        try {
            val list = ArrayList<MyBluetoothDevice>()
            val deviceList =
                bluetoothAdapter?.bondedDevices as Set<android.bluetooth.BluetoothDevice>
            deviceList.forEach {
                list.add(
                    MyBluetoothDevice(
                        it,
                        oldDevice[1] == it.address
                    )
                )
                Log.i("MyMess", "name: ${it.name}   address: ${it.address}")
            }
            listDevices.clear()
            listDevices.addAll(list)
        } catch (e: SecurityException) {

        }
    }

    private fun changeChecked(dev: MyBluetoothDevice): Unit {
        listDevices.forEach() {
            if (it.device.address != dev.device.address) it.isChecked = false
            else {
                mCoroutineScope.launch { dataStoreManager.saveData(dev) }
            }
        }
        listDevices.add(MyBluetoothDevice(dev.device, false))
        listDevices.removeLast()
        //
        // !!!
//        // set selectedDevice and init bluetoothController!
//        selectedDevice = dev.device
//        bluetoothController = BluetoothController(selectedDevice!!)

    }

    private fun registerBtPermissions() {
        btPerLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {

            }
    }

    private fun mainCheckBtPermission(context: Context) {
        if (!checkBtPermissions(context)) {
            registerBtPermissions()
            launchBtPermissions()
        }
    }

    private fun launchBtPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            btPerLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH_SCAN
                )
            )
        } else {
            btPerLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    private fun searchDevices() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //TODO
        } else {
            listDiscoveryDevices.clear()
            isSearch.value = true
            bluetoothAdapter.startDiscovery()
        }
    }

    private fun createBond(item: BluetoothDevice) {
        if (ActivityCompat.checkSelfPermission(
                myContext,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
        }
        item.createBond()
    }

    private fun connectClick(){
        if (selectedDevice != null) bluetoothController.connect(this)
        else Log.i("MyMess", "bluetoothController is null")
    }

    private fun disconnectClick(){
        if (isDeviceConnected.value) bluetoothController.closeConnection()
        else Log.i("MyMess", "isDeviceConnected.value is false")
    }


    private fun sendMessage(message: String){
        bluetoothController.sendMessage(message)
    }

    override fun onReceive(message: String) {
        runOnUiThread{
            when(message){
                BluetoothController.BLUETOOTH_CONNECTED -> {
                    isDeviceConnected.value = true
                }
                BluetoothController.BLUETOOTH_NO_CONNECTED -> {
                    isDeviceConnected.value = false
                }
                else -> {
                    stringReceiver += message
                    val l = stringReceiver.length
                    if ( l >= 9 && stringReceiver.substring(l-3, l) == "fff" ){
                        stringReceiverTemp = stringReceiver
                        stringReceiver = ""
                        decodString(stringReceiverTemp)
                    }
                }
            }
        }
    }

    private fun decodString(string: String){
        val list = string.split("fff")
        list.forEach(){
            if (it.length >= 6){
                val start = it.substring(0, 3)
                when(start){
                    "vvv" -> {
                        val s = it.substring(3, it.length)
                        try {
                            receiverVoltage.value = s.toFloat()
                        } catch (e: NumberFormatException) {
                            Log.i("MyMess", "NumberFormatException $e")
                        }
                    }
                    "aaa" -> {
                        val s = it.substring(3, it.length)
                        try {
                            receiverCurrent.value = s.toFloat()
                        } catch (e: NumberFormatException) {
                            Log.i("MyMess", "NumberFormatException $e")
                        }
                    }
                    "ppp" -> {
                        val s = it.substring(3, it.length)
                        try {
                            receiverPotencial.value = s.toFloat()
                        } catch (e: NumberFormatException) {
                            Log.i("MyMess", "NumberFormatException $e")
                        }
                    }
                    "rrr" -> {
                        val s = it.substring(3, it.length)
                        try {
                            receiverPwm.value = s.toFloat()
                        } catch (e: NumberFormatException) {
                            Log.i("MyMess", "NumberFormatException $e")
                        }
                    }
                }
            }
        }

    }


}

