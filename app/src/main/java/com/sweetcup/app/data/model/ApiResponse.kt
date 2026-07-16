package com.sweetcup.app.data.model

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: T,
    @SerializedName("message") val message: String? = null
)

data class PaginatedData<T>(
    @SerializedName("data") val data: List<T>,
    @SerializedName("links") val links: Links? = null,
    @SerializedName("meta") val meta: Meta? = null
)

data class Links(
    @SerializedName("first") val first: String? = null,
    @SerializedName("last") val last: String? = null,
    @SerializedName("prev") val prev: String? = null,
    @SerializedName("next") val next: String? = null
)

data class Meta(
    @SerializedName("current_page") val currentPage: Int? = null,
    @SerializedName("last_page") val lastPage: Int? = null,
    @SerializedName("per_page") val perPage: Int? = null,
    @SerializedName("total") val total: Int? = null
)
