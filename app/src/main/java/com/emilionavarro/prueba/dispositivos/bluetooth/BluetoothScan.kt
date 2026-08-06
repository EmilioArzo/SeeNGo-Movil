package com.emilionavarro.prueba.dispositivos.bluetooth

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emilionavarro.prueba.dispositivos.viewModelFactory

private val Background     = Color(0xFFEAE7E0)
private val Surface        = Color(0xFFF2EFEA)
private val SurfaceSelected= Color(0xFFE8E5DE)
private val OnBackground   = Color(0xFF1C1C1C)
private val Subtle         = Color(0xFF7A7A7A)
private val Accent         = Color(0xFF232320)
private val BorderColor    = Color(0xFFDDDAD3)
private val BorderSelected = Color(0xFF232320)
private val IconBg         = Color(0xFFE4E1D9)
private val CheckBg        = Color(0xFF232320)

@Composable
fun BluetoothScanScreen(
    userId: String,
    onBack: () -> Unit = {},
    onLinked: (deviceId: String) -> Unit = {},
    viewModel: BluetoothDevicesViewModel = viewModel(factory = viewModelFactory { BluetoothDevicesViewModel(userId = userId) })
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var selectedIds by remember { mutableStateOf(setOf<String>()) }
    var hasPermissions by remember { mutableStateOf(false) }

    val requiredPermissions = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        hasPermissions = results.values.all { it }
        if (hasPermissions) viewModel.startScan(context)
    }

    LaunchedEffect(Unit) { permissionLauncher.launch(requiredPermissions) }
    DisposableEffect(Unit) { onDispose { viewModel.stopScan() } }
    LaunchedEffect(uiState.linkSuccess) {
        if (uiState.linkSuccess) onLinked(uiState.newlyLinkedDeviceId ?: "new")
    }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 90.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Surface).clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.ChevronLeft, contentDescription = "Volver", tint = OnBackground, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Buscar por Bluetooth", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OnBackground)
                    Text("Dispositivos cercanos", fontSize = 12.sp, color = Subtle)
                }
            }

            Spacer(Modifier.height(20.dp))

            if (uiState.errorMessage != null) {
                Text(uiState.errorMessage!!, color = Color(0xFFA33C3C), fontSize = 13.sp)
                Spacer(Modifier.height(12.dp))
            }

            when {
                !hasPermissions ->
                    Text("Necesitamos permiso de Bluetooth para buscar dispositivos cercanos.", fontSize = 13.sp, color = Subtle)

                uiState.isScanning && uiState.devices.isEmpty() -> {
                    Box(Modifier.fillMaxWidth().padding(vertical = 30.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Accent)
                            Spacer(Modifier.height(12.dp))
                            Text("Buscando dispositivos Bluetooth...", fontSize = 13.sp, color = Subtle)
                        }
                    }
                }

                uiState.devices.isEmpty() ->
                    Text("No se encontraron dispositivos cercanos todavía.", fontSize = 13.sp, color = Subtle)

                else -> {
                    uiState.devices.forEach { device ->
                        val isSelected = device.macAddress in selectedIds
                        BleDeviceRow(
                            device = device,
                            isSelected = isSelected,
                            onClick = {
                                selectedIds = if (isSelected) selectedIds - device.macAddress else selectedIds + device.macAddress
                            }
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            OutlinedButton(
                onClick = { viewModel.startScan(context) },
                enabled = hasPermissions && !uiState.isScanning,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, BorderColor),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Surface, contentColor = OnBackground)
            ) {
                Icon(Icons.Outlined.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Volver a escanear", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }

        Box(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().background(Background)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Button(
                onClick = {
                    val selectedDevices = uiState.devices.filter { it.macAddress in selectedIds }
                    viewModel.linkDevices(selectedDevices)
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent),
                enabled = selectedIds.isNotEmpty() && !uiState.isLinking
            ) {
                if (uiState.isLinking) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    val count = selectedIds.size
                    Text("Vincular $count dispositivo${if (count != 1) "s" else ""}", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun BleDeviceRow(device: BleFoundDevice, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) SurfaceSelected else Surface)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) BorderSelected else BorderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(44.dp).background(IconBg, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.Bluetooth, contentDescription = null, tint = OnBackground, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(device.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OnBackground)
            Text("${device.macAddress} · ${device.rssi} dBm", fontSize = 12.sp, color = Subtle)
        }
        Spacer(Modifier.width(10.dp))
        if (isSelected) {
            Box(modifier = Modifier.size(28.dp).background(CheckBg, CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.Check, contentDescription = "Seleccionado", tint = Color.White, modifier = Modifier.size(16.dp))
            }
        } else {
            Box(modifier = Modifier.size(28.dp).border(1.5.dp, BorderColor, CircleShape))
        }
    }
}