package com.emilionavarro.prueba.perfil.data


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

class AppPreferences(private val context: Context) {

    companion object {
        val KEY_THEME             = stringPreferencesKey("theme")
        val KEY_LANGUAGE          = stringPreferencesKey("language")
        val KEY_ENERGY_UNIT       = stringPreferencesKey("energy_unit")
        val KEY_TEMP_UNIT         = stringPreferencesKey("temp_unit")
        val KEY_PUSH_NOTIF        = booleanPreferencesKey("push_notif")
        val KEY_HIGH_CONTRAST     = booleanPreferencesKey("high_contrast")
        val KEY_VIBRATION         = booleanPreferencesKey("vibration")
        val KEY_CONSUMPTION_ALERT = booleanPreferencesKey("consumption_alert")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences())
            else throw e
        }
        .map { prefs ->
            AppSettings(
                theme             = prefs[KEY_THEME]             ?: "Claro",
                language          = prefs[KEY_LANGUAGE]          ?: "Español",
                energyUnit        = prefs[KEY_ENERGY_UNIT]       ?: "kWh",
                tempUnit          = prefs[KEY_TEMP_UNIT]         ?: "°C",
                pushNotifications = prefs[KEY_PUSH_NOTIF]        ?: true,
                highContrast      = prefs[KEY_HIGH_CONTRAST]     ?: false,
                vibration         = prefs[KEY_VIBRATION]         ?: true,
                consumptionAlert  = prefs[KEY_CONSUMPTION_ALERT] ?: true,
            )
        }

    suspend fun setTheme(value: String)             = save(KEY_THEME, value)
    suspend fun setLanguage(value: String)          = save(KEY_LANGUAGE, value)
    suspend fun setEnergyUnit(value: String)        = save(KEY_ENERGY_UNIT, value)
    suspend fun setTempUnit(value: String)          = save(KEY_TEMP_UNIT, value)
    suspend fun setPushNotifications(value: Boolean)= save(KEY_PUSH_NOTIF, value)
    suspend fun setHighContrast(value: Boolean)     = save(KEY_HIGH_CONTRAST, value)
    suspend fun setVibration(value: Boolean)        = save(KEY_VIBRATION, value)
    suspend fun setConsumptionAlert(value: Boolean) = save(KEY_CONSUMPTION_ALERT, value)

    private suspend fun <T> save(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { prefs -> prefs[key] = value }
    }
}

data class AppSettings(
    val theme: String              = "Claro",
    val language: String           = "Español",
    val energyUnit: String         = "kWh",
    val tempUnit: String           = "°C",
    val pushNotifications: Boolean = true,
    val highContrast: Boolean      = false,
    val vibration: Boolean         = true,
    val consumptionAlert: Boolean  = true,
)