package com.emilionavarro.prueba.dispositivos



import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras

/**
 * Pequeño helper para instanciar ViewModels con parámetros propios
 * (userId, deviceId) sin depender de Hilt/Dagger.
 *
 * Uso:
 *   viewModel(factory = viewModelFactory { DevicesViewModel(userId = "123") })
 */
fun <T : ViewModel> viewModelFactory(build: () -> T): ViewModelProvider.Factory =
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <VM : ViewModel> create(modelClass: Class<VM>, extras: CreationExtras): VM {
            return build() as VM
        }
    }