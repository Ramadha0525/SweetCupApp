package com.sweetcup.app.data.model

import com.google.gson.annotations.SerializedName

data class PaymentSettings(
    @SerializedName("shipping_cost") val shippingCost: String? = null,
    @SerializedName("manual_payment_enabled") val manualPaymentEnabled: String? = null,
    @SerializedName("cod_enabled") val codEnabled: String? = null,
    @SerializedName("ewallet_enabled") val ewalletEnabled: String? = null,
    @SerializedName("ewallet_name") val ewalletName: String? = null,
    @SerializedName("ewallet_number") val ewalletNumber: String? = null,
    @SerializedName("ewallet_owner") val ewalletOwner: String? = null,
    @SerializedName("qris_enabled") val qrisEnabled: String? = null,
    @SerializedName("qris_image") val qrisImage: String? = null
)

data class WebSettings(
    @SerializedName("store_name") val storeName: String? = null,
    @SerializedName("theme_primary") val themePrimary: String? = null,
    @SerializedName("theme_secondary") val themeSecondary: String? = null
)
