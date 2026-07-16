package com.sweetcup.app.data.model

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("price") val price: Double,
    @SerializedName("image") val image: String? = null,
    @SerializedName("category") val category: Category? = null,
    @SerializedName("reviews") val reviews: List<Review>? = null,
    @SerializedName("average_rating") val averageRating: Double? = null,
    @SerializedName("reviews_count") val reviewsCount: Int? = null,
    @SerializedName("stock") val stock: Int? = null
)
