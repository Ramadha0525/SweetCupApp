package com.sweetcup.app.data.model

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("icon") val icon: String? = null,
    @SerializedName("slug") val slug: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("products_count") val productsCount: Int? = null
)
