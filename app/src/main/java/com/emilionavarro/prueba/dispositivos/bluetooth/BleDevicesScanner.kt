package com.emilionavarro.prueba.dispositivos.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

data class BleFoundDevice(
    val macAddress: String,
    val name: String,
    val rssi: Int
)

/** Envoltura simple sobre BluetoothLeScanner, expuesta como Flow. */
class BleDeviceScanner(context: Context) {

    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val adapter: BluetoothAdapter? = bluetoothManager.adapter

    fun isBluetoothEnabled(): Boolean = adapter?.isEnabled == true

    @SuppressLint("MissingPermission") // se valida permiso antes de llamar desde la UI
    fun scan(): Flow<BleFoundDevice> = callbackFlow {
        val scanner = adapter?.bluetoothLeScanner
        if (scanner == null) {
            close(IllegalStateException("Bluetooth LE no disponible en este dispositivo."))
            return@callbackFlow
        }

        val callback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val device = result.device
                val name = device.name ?: result.scanRecord?.deviceName ?: "Dispositivo Bluetooth"
                trySend(
                    BleFoundDevice(
                        macAddress = device.address,
                        name = name,
                        rssi = result.rssi
                    )
                )
            }

            override fun onScanFailed(errorCode: Int) {
                close(IllegalStateException("Error de escaneo BLE (código $errorCode)."))
            }
        }

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        scanner.startScan(null, settings, callback)

        awaitClose { scanner.stopScan(callback) }
    }
}