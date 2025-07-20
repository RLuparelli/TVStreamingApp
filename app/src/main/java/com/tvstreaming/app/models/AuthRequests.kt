package com.tvstreaming.app.models

import com.google.gson.annotations.SerializedName

data class DeviceAuthRequest(
    @SerializedName("device_id")
    val deviceId: String,
    
    @SerializedName("device_type")
    val deviceType: String,
    
    @SerializedName("platform")
    val platform: String,
    
    @SerializedName("app_version")
    val appVersion: String
)

data class CodeAuthRequest(
    @SerializedName("code")
    val code: String,
    
    @SerializedName("device_id")
    val deviceId: String,
    
    @SerializedName("platform")
    val platform: String
)