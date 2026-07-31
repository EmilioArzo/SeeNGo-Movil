package com.emilionavarro.prueba.perfil.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.emilionavarro.prueba.perfil.data.AppPreferences
import com.emilionavarro.prueba.perfil.data.AppSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val prefs: AppPreferences) : ViewModel() {

    // Single source of truth — all settings as StateFlow
    val state: StateFlow<AppSettings> = prefs.settingsFlow.stateIn(
        scope         = viewModelScope,
        started       = SharingStarted.WhileSubscribed(5_000),
        initialValue  = AppSettings()
    )

    // ── Theme ─────────────────────────────────────────────────────────────────
    fun setTheme(value: String) = viewModelScope.launch { prefs.setTheme(value) }

    // ── Language ──────────────────────────────────────────────────────────────
    fun setLanguage(value: String) = viewModelScope.launch { prefs.setLanguage(value) }

    // ── Units ─────────────────────────────────────────────────────────────────
    fun setEnergyUnit(value: String) = viewModelScope.launch { prefs.setEnergyUnit(value) }
    fun setTempUnit(value: String)   = viewModelScope.launch { prefs.setTempUnit(value) }

    // ── Notifications ─────────────────────────────────────────────────────────
    fun setPushNotifications(value: Boolean) = viewModelScope.launch { prefs.setPushNotifications(value) }
    fun setConsumptionAlert(value: Boolean)  = viewModelScope.launch { prefs.setConsumptionAlert(value) }

    // ── Accessibility ─────────────────────────────────────────────────────────
    fun setHighContrast(value: Boolean) = viewModelScope.launch { prefs.setHighContrast(value) }
    fun setVibration(value: Boolean)    = viewModelScope.launch { prefs.setVibration(value) }
}

class SettingsViewModelFactory(
    private val prefs: AppPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SettingsViewModel(prefs) as T
    }
}