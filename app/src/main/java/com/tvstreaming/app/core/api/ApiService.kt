package com.tvstreaming.app.core.api

import com.tvstreaming.app.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // Autenticação
    @POST("/auth/device")
    suspend fun authenticateDevice(@Body request: DeviceAuthRequest): Response<AuthResponse>
    
    @POST("/auth/code")
    suspend fun authenticateCode(@Body request: CodeAuthRequest): Response<AuthResponse>
    
    @POST("/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<AuthResponse>
    
    // Conteúdo
    @GET("/content/categories")
    suspend fun getCategories(@Header("Authorization") token: String): Response<List<Category>>
    
    @GET("/content/{categoryId}")
    suspend fun getContentByCategory(
        @Path("categoryId") categoryId: String,
        @Header("Authorization") token: String
    ): Response<List<ContentItem>>
    
    @GET("/content/details/{contentId}")
    suspend fun getContentDetails(
        @Path("contentId") contentId: String,
        @Header("Authorization") token: String
    ): Response<ContentDetails>
    
    @GET("/content/search")
    suspend fun searchContent(
        @Query("q") query: String,
        @Query("category") category: String? = null,
        @Header("Authorization") token: String
    ): Response<List<ContentItem>>
    
    // Usuário
    @GET("/user/profile")
    suspend fun getUserProfile(@Header("Authorization") token: String): Response<UserInfo>
    
    @POST("/user/watch-progress")
    suspend fun updateWatchProgress(
        @Body request: WatchProgressRequest,
        @Header("Authorization") token: String
    ): Response<Unit>
    
    @GET("/user/watch-progress/{contentId}")
    suspend fun getWatchProgress(
        @Path("contentId") contentId: String,
        @Header("Authorization") token: String
    ): Response<WatchProgressResponse>
    
    // Pagamento
    @POST("/payment/generate-qr")
    suspend fun generatePaymentQr(
        @Body request: PaymentQrRequest,
        @Header("Authorization") token: String
    ): Response<PaymentQrResponse>
    
    @GET("/payment/status/{paymentId}")
    suspend fun getPaymentStatus(
        @Path("paymentId") paymentId: String,
        @Header("Authorization") token: String
    ): Response<PaymentStatusResponse>
}

// Request/Response models
data class DeviceAuthRequest(
    val macAddress: String,
    val deviceInfo: Map<String, String>,
    val tenantId: String? = null
)

data class CodeAuthRequest(
    val code: String,
    val deviceInfo: Map<String, String>
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class WatchProgressRequest(
    val contentId: String,
    val position: Long,
    val duration: Long
)

data class WatchProgressResponse(
    val contentId: String,
    val position: Long,
    val lastWatched: Long
)

data class PaymentQrRequest(
    val amount: Double,
    val currency: String = "BRL",
    val planId: String,
    val deviceId: String
)

data class PaymentQrResponse(
    val qrCode: String,
    val paymentId: String,
    val amount: Double,
    val expirationTime: Long
)

data class PaymentStatusResponse(
    val paymentId: String,
    val status: String, // "pending", "completed", "failed", "expired"
    val completedAt: Long?
)