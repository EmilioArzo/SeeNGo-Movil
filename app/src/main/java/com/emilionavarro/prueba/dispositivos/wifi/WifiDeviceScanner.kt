package com.emilionavarro.prueba.dispositivos.wifi

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.security.MessageDigest

data class WifiFoundDevice(
    val macAddress: String, // pseudo-MAC derivado del nombre del servicio (ver nota abajo)
    val name: String,
    val localIp: String
)

/**
 * Descubrimiento mDNS con NsdManager. Busca servicios "_http._tcp" (lo que
 * suelen anunciar los Shelly).
 *
 * NOTA IMPORTANTE: NsdManager no expone la MAC real del dispositivo, solo su
 * nombre de servicio, host y puerto. El backend indexa dispositivos por
 * macAddress, así que aquí derivamos un identificador estable a partir del
 * nombre del servicio (que en Shelly normalmente ya incluye el sufijo de la
 * MAC, ej. "shellyplug-s-3F2A"). Si tu hardware no sigue ese patrón, ajusta
 * `deriveMacLikeId`.
 */
class WifiDeviceScanner(private val context: Context) {

    private val serviceType = "_http._tcp."

    fun scan(): Flow<WifiFoundDevice> = callbackFlow {
        val nsdManager = context.getSystemService(Context.NSD_SERVICE) as? NsdManager
        if (nsdManager == null) {
            close(IllegalStateException("NSD no disponible en este dispositivo."))
            return@callbackFlow
        }

        val resolveListener = object : NsdManager.ResolveListener {
            override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) = Unit
            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                trySend(
                    WifiFoundDevice(
                        macAddress = deriveMacLikeId(serviceInfo.serviceName),
                        name = serviceInfo.serviceName,
                        localIp = serviceInfo.host?.hostAddress ?: "desconocida"
                    )
                )
            }
        }

        val discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(serviceType: String) = Unit
            override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                try {
                    nsdManager.resolveService(serviceInfo, resolveListener)
                } catch (_: Exception) { /* resolución en curso, ignorar duplicado */ }
            }
            override fun onServiceLost(serviceInfo: NsdServiceInfo) = Unit
            override fun onDiscoveryStopped(serviceType: String) = Unit
            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                close(IllegalStateException("Error al iniciar descubrimiento WiFi (código $errorCode)."))
            }
            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) = Unit
        }

        nsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, discoveryListener)

        awaitClose {
            try { nsdManager.stopServiceDiscovery(discoveryListener) } catch (_: Exception) {}
        }
    }

    /** Deriva un identificador estable de 12 hex chars a partir del nombre del servicio. */
    private fun deriveMacLikeId(serviceName: String): String {
        // Si el nombre ya trae un sufijo hex tipo Shelly (ej. "shellyplug-s-3F2A"), úsalo.
        val hexSuffix = Regex("[0-9A-Fa-f]{4,12}$").find(serviceName)?.value
        if (hexSuffix != null && hexSuffix.length >= 6) return hexSuffix.uppercase()

        // Fallback: hash corto y determinista del nombre completo.
        val digest = MessageDigest.getInstance("SHA-1").digest(serviceName.toByteArray())
        return digest.joinToString("") { "%02X".format(it) }.take(12)
    }
}