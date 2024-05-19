package com.vitali.bluetoothmod.screens

import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitali.bluetoothmod.MainActivity
import com.vitali.bluetoothmod.R
import com.vitali.bluetoothmod.ui.theme.MyBlue





//@Preview(showBackground = true)
@Composable
fun ShowBluetooth(
    onClick: (dev: MyBluetoothDevice) -> Unit,
    myContext: MainActivity,
    statusBluetooth: MutableState<List<Any>>,
    isSearch: MutableState<Boolean>,
    changeBtStatus: (Boolean) -> Unit,
    listDevices: MutableList<MyBluetoothDevice>,
    listDiscoveryDevices: MutableList<MyBluetoothDevice>,
    changeChecked: (MyBluetoothDevice) -> Unit,
    searchDevices: () -> Unit,
    createBond: (item: BluetoothDevice) -> Unit
) {


//    Image(
//        painter = painterResource(id = R.drawable.debian_wallpaper),
//        contentDescription = "im_back",
//        modifier = Modifier
//            .fillMaxSize()
//            .alpha(0.5f),
//        contentScale = ContentScale.FillBounds
//    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MyBlue)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .padding(10.dp)

        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = statusBluetooth.value[1] as String,
                        style = TextStyle(fontSize = 18.sp),
                        color = statusBluetooth.value[2] as Color
                    )
                    Switch(
                        checked = statusBluetooth.value[0] as Boolean,
                        onCheckedChange = {
                            changeBtStatus(it)
                        }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Сохраненные устройства",
                        style = TextStyle(fontSize = 18.sp),
                        color = Color.Black
                    )
                    Icon(painter = painterResource(id = R.drawable.baseline_bluetooth_24),
                        contentDescription = null,
                        tint = statusBluetooth.value[2] as Color)
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight(0.8f)
                        .padding(10.dp)
                ) {
                    itemsIndexed(
                        listDevices
                    ) { _, item ->
                        RowBluetooth(myContext, item = item, changeChecked)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Button(onClick = {
                        var isCh = false
                        var dev: MyBluetoothDevice? = null
                        listDevices.forEach{
                            if ( it.isChecked ) {
                                dev = it
                                isCh = true
                            }
                        }
                        if (isCh && (dev != null) ) onClick(dev!!)
                    }) {
                        Text(text = "Выбрать устройство")
                    }
                }
            }
        }

        Divider( modifier = Modifier.height(5.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Поиск устройств",
                    style = TextStyle(fontSize = 18.sp),
                    color = Color.Black
                )
                if (isSearch.value) CircularProgressIndicator()
                else{
                    Button(
                        onClick = { searchDevices() },
                        colors = ButtonDefaults.buttonColors(containerColor = MyBlue),
                        border = BorderStroke(2.dp, Color.Black),

                    ) {
                        Icon(painter = painterResource(id = R.drawable.baseline_bluetooth_24),
                            contentDescription = null,
                            tint = statusBluetooth.value[2] as Color
                        )
                    }
                }

            }


            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 10.dp, top = 10.dp, end = 10.dp)
            ) {
                itemsIndexed(
                    listDiscoveryDevices
                ){ _, item ->
                    RowDiscoveryBluetooth(myContext = myContext, item = item, createBond)
                }
            }




        }

    }
}



