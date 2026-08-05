package com.emilionavarro.prueba.dispositivos.data

enum class DiscoverySource { WIFI, BLUETOOTH }

/** Modelo único para cualquier dispositivo detectado, sin importar el transporte. */
data class DiscoveredDevice(
    val macAddress: String,
    val name: String,
    val source: DiscoverySource,
    val extraInfo: String // IP local (WiFi) o RSSI (Bluetooth), solo para mostrar
)