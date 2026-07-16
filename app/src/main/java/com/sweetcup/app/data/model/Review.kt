package com.sweetcup.app.data.model

import com.google.gson.annotations.SerializedName

data class Review(
    @SerializedName("id") val id: Int,
    @SerializedName("user") val user: User? = null,
    @SerializedName("rating") val rating: Int,
    @SerializedName("comment") val comment: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

data class ReviewRequest(
    @SerializedName("product_id") val productId: Int,
    @SerializedName("rating") val rating: Int,
    @SerializedName("comment") val comment: String? = null
)
