package com.emilionavarro.prueba.perfil.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

data class ConsumptionSummaryDto(
    val userId: String,
    val period: String?,
    val totalKwh: Double,
    val totalEvents: Int,
    val deviceCount: Int,
    val sessionsCount: Int
)

interface AnalyticsApiService {
    // GET /api/analytics/consumption/summary?userId=...&period=week|month|year
    @GET("api/analytics/consumption/summary")
    suspend fun getConsumptionSummary(
        @Query("userId") userId: String,
        @Query("period") period: String = "month"
    ): Response<ConsumptionSummaryDto>
}