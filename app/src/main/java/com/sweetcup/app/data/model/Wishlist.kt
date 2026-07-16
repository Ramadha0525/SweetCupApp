package com.sweetcup.app.data.model

import com.google.gson.annotations.SerializedName

data class WishlistRequest(
    @SerializedName("product_id") val productId: Int
)

data class WishlistResponse(
    @SerializedName("status") val status: String
)
