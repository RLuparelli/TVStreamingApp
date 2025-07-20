package com.tvstreaming.app.models

import com.google.gson.annotations.SerializedName

data class ContentListResponse(
    @SerializedName("contents")
    val contents: List<ContentItem>,
    
    @SerializedName("page")
    val page: Int,
    
    @SerializedName("total_pages")
    val totalPages: Int,
    
    @SerializedName("total_items")
    val totalItems: Int
)