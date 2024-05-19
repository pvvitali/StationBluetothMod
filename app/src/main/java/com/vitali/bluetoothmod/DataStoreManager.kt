package com.vitali.bluetoothmod

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.vitali.bluetoothmod.screens.MyBluetoothDevice
import kotlinx.coroutines.flow.map

private val Context.datastore: DataStore<Preferences> by preferencesDataStore("data_store")

class DataStoreManager(private val context: Context) {
    suspend fun saveData(dev: MyBluetoothDevice){
        context.datastore.edit { pref ->
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {}
            pref[stringPreferencesKey("name")] = dev.device.name
            pref[stringPreferencesKey("address")] = dev.device.address
            pref[booleanPreferencesKey("isChecked")] = dev.isChecked
        }
    }

    fun getDataStore() = context.datastore.data.map { pref ->
        return@map listOf<Any>(
            pref[stringPreferencesKey("name")] ?: "",
            pref[stringPreferencesKey("address")] ?: "",
            pref[booleanPreferencesKey("isChecked")] ?: false
        )
    }
}