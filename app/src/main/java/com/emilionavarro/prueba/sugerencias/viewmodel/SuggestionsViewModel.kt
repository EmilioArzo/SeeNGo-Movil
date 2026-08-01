package com.emilionavarro.prueba.sugerencias.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.emilionavarro.prueba.sugerencias.data.network.CreateRoutineDto
import com.emilionavarro.prueba.sugerencias.data.network.RoutineDto
import com.emilionavarro.prueba.sugerencias.data.network.SugerenciasRetrofitClient
import com.emilionavarro.prueba.sugerencias.data.network.SuggestionDto
import com.emilionavarro.prueba.sugerencias.data.repository.SuggestionsRepository
import com.emilionavarro.prueba.sugerencias.data.repository.SuggestionsResult
import kotlinx.coroutines.launch

data class SuggestionsUiState(
    val isLoading: Boolean = false,
    val suggestions: List<SuggestionDto> = emptyList(),
    val error: String? = null,
    val selectedSuggestion: SuggestionDto? = null,
    val isActivating: Boolean = false,
    val activatedRoutine: RoutineDto? = null,
    val activateError: String? = null
)

/**
 * ViewModel compartido para todo el flujo Sugerencias → Detalle → Rutina activada.
 *
 * Se instancia UNA sola vez en AppNavHost (no una por pantalla) porque el backend
 * no expone GET /api/suggestions/{id}: la única forma de mostrar el detalle de una
 * sugerencia es conservarla en memoria desde que se cargó la lista completa.
 */
class SuggestionsViewModel(
    private val repository: SuggestionsRepository = SuggestionsRepository(SugerenciasRetrofitClient.api)
) : ViewModel() {

    var uiState by mutableStateOf(SuggestionsUiState())
        private set

    fun loadSuggestions(userId: String, forceReload: Boolean = false) {
        if (userId.isBlank()) {
            uiState = uiState.copy(error = "No se encontró el usuario en sesión.")
            return
        }
        if (uiState.suggestions.isNotEmpty() && !forceReload) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            when (val result = repository.getActiveSuggestions(userId)) {
                is SuggestionsResult.Success ->
                    uiState = uiState.copy(isLoading = false, suggestions = result.data)
                is SuggestionsResult.Error ->
                    uiState = uiState.copy(isLoading = false, error = result.message)
                else -> Unit
            }
        }
    }

    /** Selecciona una sugerencia ya cargada en la lista y la marca como vista en el backend. */
    fun selectSuggestion(id: String) {
        val found = uiState.suggestions.find { it.id == id }
        uiState = uiState.copy(selectedSuggestion = found, activatedRoutine = null, activateError = null)

        if (found != null && !found.isViewed) {
            viewModelScope.launch {
                repository.markSuggestionViewed(id)
                uiState = uiState.copy(
                    suggestions = uiState.suggestions.map { s ->
                        if (s.id == id) s.copy(isViewed = true) else s
                    }
                )
            }
        }
    }

    fun discardSelected() {
        uiState = uiState.copy(selectedSuggestion = null)
    }

    /** Convierte la sugerencia seleccionada en una rutina real vía POST /api/routines */
    fun activateSelectedSuggestion(userId: String) {
        val suggestion = uiState.selectedSuggestion ?: return
        viewModelScope.launch {
            uiState = uiState.copy(isActivating = true, activateError = null)
            val dto = CreateRoutineDto(
                userId = userId,
                name = suggestion.recommendationText,
                description = "Generada a partir del análisis de tu patrón de consumo (${suggestion.assignedCluster}).",
                triggerType = "cluster",
                triggerValue = suggestion.assignedCluster,
                actions = emptyList()
            )
            when (val result = repository.createRoutine(dto)) {
                is SuggestionsResult.Success ->
                    uiState = uiState.copy(isActivating = false, activatedRoutine = result.data)
                is SuggestionsResult.Error ->
                    uiState = uiState.copy(isActivating = false, activateError = result.message)
                else -> Unit
            }
        }
    }

    fun clearActivation() {
        uiState = uiState.copy(activatedRoutine = null, selectedSuggestion = null)
    }
}

class SuggestionsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SuggestionsViewModel() as T
    }
}