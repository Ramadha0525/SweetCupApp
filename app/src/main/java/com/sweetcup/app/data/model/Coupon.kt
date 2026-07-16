package com.sweetcup.app.data.model

import com.google.gson.annotations.SerializedName

data class Coupon(
    @SerializedName("code") val code: String,
    @SerializedName("type") val type: String,
    @SerializedName("value") val value: Double
)

data class CouponApplyRequest(
    @SerializedName("code") val code: String
)
