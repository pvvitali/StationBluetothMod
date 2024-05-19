package com.vitali.bluetoothmod.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.vitali.bluetoothmod.MainActivity

@Composable
fun RowBluetooth(
    myContext: MainActivity,
    item: MyBluetoothDevice,
    changeChecked: (MyBluetoothDevice) -> Unit
){
    val checkerState = remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier.padding(5.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 5.dp, end = 5.dp),
            horizontalArrangement = Arrangement.Absolute.SpaceBetween
        ) {
            Column {
                if (ActivityCompat.checkSelfPermission(
                        myContext,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {}
                Text(text = "Имя: " + item.device.name)
                Text(text = "Мак адрес: " + item.device.address, fontSize = 14.sp)
            }
            Checkbox(checked = item.isChecked, onCheckedChange = {
                item.isChecked = it
                changeChecked( item )
            } )
        }
    }
}