package com.vitali.bluetoothmod


import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat

@Composable
fun ScreenMain(
    onClick: () -> Unit,
    device: BluetoothDevice?,
    connectClick: () -> Unit,
    disconnectClick: () -> Unit,
    sendMessage: (message: String) -> Unit,
    isDeviceConnected: MutableState<Boolean>,
    receiverVoltage: MutableFloatState,
    receiverCurrent: MutableFloatState,
    receiverPotencial: MutableFloatState,
    receiverPwm: MutableFloatState
) {
    var textDevice = remember {
        mutableStateOf("Устройство не выбранно")
    }
//
//    var sliderPotencial = remember {
//        mutableFloatStateOf(0.9f)
//    }
//
//    var sliderCurent = remember {
//        mutableFloatStateOf(5f)
//    }
//
//    var sliderVoltage = remember {
//        mutableFloatStateOf(15f)
//    }

    var voltageValue: Float = 0f
    var stringVoltageValue: String = ""




    if (ActivityCompat.checkSelfPermission(
            LocalContext.current,
            Manifest.permission.BLUETOOTH_CONNECT
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: Consider calling
        return
    }

    if (device == null) {
        textDevice.value = "Устройство не выбрано"
    } else {
        textDevice.value = device.name + "  " + device.address
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) ,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Button(
            onClick = { onClick() },
            modifier = Modifier.fillMaxWidth().padding( start = 10.dp, top = 15.dp, end = 10.dp)
        ) {
            Text(text = "Выбор устройств")
        }
        Button(
            onClick = { if(!isDeviceConnected.value) connectClick() else disconnectClick() },
            modifier = Modifier.fillMaxWidth().padding( start = 10.dp, top = 15.dp, end = 10.dp)
        ) {
            if(!isDeviceConnected.value) Text(text = "Подключить") else Text(text = "Отключить")
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(0.7f),
                text = textDevice.value,
                fontSize = 22.sp
            )
            if (isDeviceConnected.value){
                Icon(painter = painterResource(id = R.drawable.baseline_cell_tower_24),
                    modifier = Modifier.size(50.dp),
                    contentDescription = null,
                    tint = Color.Green
                )
            } else {
                Icon(painter = painterResource(id = R.drawable.baseline_cancel_24),
                    modifier = Modifier.size(50.dp),
                    contentDescription = null,
                    tint = Color.Red
                )
            }
        }

        // Potencial ----------------------------------------------------------------------
        Spacer(modifier = Modifier.height(30.dp))

        Row {

            Text(
                text = "Защитный потенциал:  ",
                fontSize = 18.sp
            )
            Text(
                text = receiverPotencial.floatValue.toString() + " V",
                fontSize = 18.sp,
                color = Color.Blue
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Slider(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp),
            value = receiverPotencial.floatValue,
            onValueChange = {receiverPotencial.floatValue = it},
            valueRange = 0f..3f,
            enabled = false
            )


        // Curкent ----------------------------------------------------------------------
        Spacer(modifier = Modifier.height(30.dp))

        Row {

            Text(
                text = "Ток:  ",
                fontSize = 18.sp
            )
            Text(
                text = receiverCurrent.floatValue.toString() + " A",
                fontSize = 18.sp,
                color = Color.Blue
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Slider(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp),
            value = receiverCurrent.floatValue,
            onValueChange = {receiverCurrent.floatValue = it},
            valueRange = 0f..20f,
            enabled = false
        )


        // Voltage ----------------------------------------------------------------------
        Spacer(modifier = Modifier.height(30.dp))

        Row {

            Text(
                text = "Напряжение:  ",
                fontSize = 18.sp
            )
            Text(
                text = receiverVoltage.floatValue.toString() + " V    ",
                fontSize = 18.sp,
                color = Color.Blue
            )
            Text(
                text = receiverPwm.floatValue.toString() + "%",
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Slider(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp),
            value = receiverPwm.floatValue,
            onValueChange = {
                voltageValue = (Math.round(it * 10.0) / 10.0).toFloat()
                receiverPwm.floatValue = voltageValue
                stringVoltageValue = "rrr" + voltageValue.toString() + "fff"
                sendMessage(stringVoltageValue)},
            valueRange = 0f..100f,
            enabled = isDeviceConnected.value
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween){
            Button(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(start = 10.dp),
                onClick = {
                    voltageValue = (Math.round((receiverPwm.floatValue - 0.1f) * 10.0) / 10.0).toFloat()
                    receiverPwm.floatValue = voltageValue
                    stringVoltageValue = "rrr" + voltageValue.toString() + "fff"
                    sendMessage(stringVoltageValue)
                },
                enabled = isDeviceConnected.value) {
                Text(text = "<<")
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp),
                onClick = {
                    voltageValue = (Math.round((receiverPwm.floatValue + 0.1f) * 10.0) / 10.0).toFloat()
                    receiverPwm.floatValue = voltageValue
                    stringVoltageValue = "rrr" + voltageValue.toString() + "fff"
                    sendMessage(stringVoltageValue)
                },
                enabled = isDeviceConnected.value) {
                Text(text = ">>")
            }
        }




        // для отладки
        Spacer(modifier = Modifier.height(30.dp))
        Button(
            onClick = { sendMessage("ggg___fff") },
            enabled = isDeviceConnected.value ) {
            Text(text = "Отправить данные")
        }
    }
}