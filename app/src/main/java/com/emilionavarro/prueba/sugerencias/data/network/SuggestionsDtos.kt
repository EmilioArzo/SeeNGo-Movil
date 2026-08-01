package com.emilionavarro.prueba.sugerencias.data.network

// ==========================================
// PREDICTIVE SUGGESTIONS
// ==========================================

data class SuggestionDto(
    val id: String? = null,
    val userId: String = "",
    val assignedCluster: String = "",
    val recommendationText: String = "",
    val projectedKwhSaving: Double = 0.0,
    val isViewed: Boolean = false,
    val createdAt: String? = null
)

// ==========================================
// ROUTINES
// ==========================================

data class RoutineActionDto(
    val deviceId: String,
    val action: String,
    val value: String? = null
)

data class RoutineDto(
    val id: String? = null,
    val userId: String = "",
    val name: String = "",
    val description: String? = null,
    val triggerType: String = "",
    val triggerValue: String = "",
    val actions: List<RoutineActionDto> = emptyList(),
    val isActive: Boolean = true,
    val createdAt: String? = null
)

data class CreateRoutineDto(
    val userId: String,
    val name: String,
    val description: String?,
    val triggerType: String,
    val triggerValue: String,
    val actions: List<RoutineActionDto>
)

data class UpdateRoutineDto(
    val name: String,
    val description: String?,
    val isActive: Boolean
)

data class ExecuteRoutineResponseDto(
    val message: String,
    val actions: List<RoutineActionDto> = emptyList()
)

// ==========================================
// SHARED
// ==========================================

data class MessageResponseDto(
    val message: String
)