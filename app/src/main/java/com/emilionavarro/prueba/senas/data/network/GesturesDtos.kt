package com.emilionavarro.prueba.senas.data.network

data class GestureDto(
    val id: String? = null,
    val userId: String = "",
    val name: String = "",
    val gestureData: String? = null,
    val linkedDeviceId: String? = null,
    val linkedAction: String? = null,
    val createdAt: String? = null
)

data class CreateGestureDto(
    val userId: String,
    val name: String,
    val gestureData: String? = null,
    val linkedDeviceId: String? = null,
    val linkedAction: String? = null
)

data class UpdateGestureDto(
    val name: String,
    val linkedDeviceId: String?,
    val linkedAction: String?
)

data class LinkGestureDto(
    val deviceId: String,
    val action: String
)

data class MessageResponseDto(
    val message: String
)