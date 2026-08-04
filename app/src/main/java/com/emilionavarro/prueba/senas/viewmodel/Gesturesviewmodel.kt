package com.emilionavarro.prueba.senas.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.emilionavarro.prueba.senas.data.network.CreateGestureDto
import com.emilionavarro.prueba.senas.data.network.GestureDto
import com.emilionavarro.prueba.senas.data.network.GesturesRetrofitClient
import com.emilionavarro.prueba.senas.data.network.UpdateGestureDto
import com.emilionavarro.prueba.senas.data.repository.GesturesRepository
import com.emilionavarro.prueba.senas.data.repository.GesturesResult
import kotlinx.coroutines.launch

data class GesturesUiState(
    val isLoading: Boolean = false,
    val gestures: List<GestureDto> = emptyList(),
    val error: String? = null,
    val selectedGesture: GestureDto? = null,
    val isSaving: Boolean = false,
    val saveError: String? = null,
    val savedGesture: GestureDto? = null,
    val isDeleting: Boolean = false,
    val wasDeleted: Boolean = false
)

/**
 * ViewModel compartido para todo el flujo Señas → Detalle → Configurar → Guardado.
 *
 * Al igual que en Sugerencias, el backend no expone GET /api/gestures/{id}, así que
 * el detalle de un gesto se obtiene de la lista ya cargada en memoria y se instancia
 * UNA sola vez en AppNavHost.
 */
class GesturesViewModel(
    private val repository: GesturesRepository = GesturesRepository(GesturesRetrofitClient.api)
) : ViewModel() {

    var uiState by mutableStateOf(GesturesUiState())
        private set

    fun loadGestures(userId: String, forceReload: Boolean = false) {
        if (userId.isBlank()) {
            uiState = uiState.copy(error = "No se encontró el usuario en sesión.")
            return
        }
        if (uiState.gestures.isNotEmpty() && !forceReload) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            when (val result = repository.getGestures(userId)) {
                is GesturesResult.Success ->
                    uiState = uiState.copy(isLoading = false, gestures = result.data)
                is GesturesResult.Error ->
                    uiState = uiState.copy(isLoading = false, error = result.message)
                else -> Unit
            }
        }
    }

    /** Selecciona un gesto ya cargado en la lista (para pantalla de Detalle / Configurar). */
    fun selectGesture(id: String) {
        val found = uiState.gestures.find { it.id == id }
        uiState = uiState.copy(selectedGesture = found, savedGesture = null, saveError = null)
    }

    /** Limpia la selección para indicar que se va a crear un gesto nuevo. */
    fun startNewGesture() {
        uiState = uiState.copy(selectedGesture = null, savedGesture = null, saveError = null)
    }

    /**
     * Crea (si no había gesto seleccionado) o actualiza (si lo había) el nombre,
     * dispositivo vinculado y acción vinculada del gesto.
     */
    fun saveGesture(userId: String, name: String, linkedDeviceId: String?, linkedAction: String?) {
        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, saveError = null)
            val existing = uiState.selectedGesture

            if (existing?.id != null) {
                when (val result = repository.updateGesture(
                    existing.id,
                    UpdateGestureDto(name = name, linkedDeviceId = linkedDeviceId, linkedAction = linkedAction)
                )) {
                    is GesturesResult.Success -> {
                        // El backend devuelve solo un mensaje de confirmación, no el
                        // documento actualizado; reflejamos el cambio localmente.
                        val updated = existing.copy(
                            name = name,
                            linkedDeviceId = linkedDeviceId,
                            linkedAction = linkedAction
                        )
                        uiState = uiState.copy(
                            isSaving = false,
                            savedGesture = updated,
                            selectedGesture = updated,
                            gestures = uiState.gestures.map { if (it.id == updated.id) updated else it }
                        )
                    }
                    is GesturesResult.Error ->
                        uiState = uiState.copy(isSaving = false, saveError = result.message)
                    else -> Unit
                }
            } else {
                val dto = CreateGestureDto(
                    userId = userId,
                    name = name,
                    gestureData = null,
                    linkedDeviceId = linkedDeviceId,
                    linkedAction = linkedAction
                )
                when (val result = repository.createGesture(dto)) {
                    is GesturesResult.Success -> {
                        val created = result.data
                        uiState = uiState.copy(
                            isSaving = false,
                            savedGesture = created,
                            selectedGesture = created,
                            gestures = uiState.gestures + created
                        )
                    }
                    is GesturesResult.Error ->
                        uiState = uiState.copy(isSaving = false, saveError = result.message)
                    else -> Unit
                }
            }
        }
    }

    fun deleteSelected(onDeleted: () -> Unit) {
        val id = uiState.selectedGesture?.id ?: return
        viewModelScope.launch {
            uiState = uiState.copy(isDeleting = true)
            when (val result = repository.deleteGesture(id)) {
                is GesturesResult.Success -> {
                    uiState = uiState.copy(
                        isDeleting = false,
                        wasDeleted = true,
                        selectedGesture = null,
                        gestures = uiState.gestures.filterNot { it.id == id }
                    )
                    onDeleted()
                }
                is GesturesResult.Error ->
                    uiState = uiState.copy(isDeleting = false, error = result.message)
                else -> Unit
            }
        }
    }

    fun clearSaved() {
        uiState = uiState.copy(savedGesture = null, selectedGesture = null, wasDeleted = false)
    }
}

class GesturesViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return GesturesViewModel() as T
    }
}