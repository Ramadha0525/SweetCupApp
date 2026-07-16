package com.sweetcup.app.data.model

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("id") val id: Int,
    @SerializedName("order_code") val orderCode: String,
    @SerializedName("status") val status: String,
    @SerializedName("total_amount") val totalAmount: Double,
    @SerializedName("unique_amount") val uniqueAmount: Double? = null,
    @SerializedName("unique_code") val uniqueCode: Int? = null,
    @SerializedName("payment_method") val paymentMethod: String? = null,
    @SerializedName("shipping_address") val shippingAddress: String? = null,
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("items") val items: List<OrderItem>? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("proof_image") val proofImage: String? = null,
    @SerializedName("tracking_number") val trackingNumber: String? = null
)

data class OrderItem(
    @SerializedName("id") val id: Int,
    @SerializedName("product_name") val productName: String,
    @SerializedName("price") val price: Double,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("image") val image: String? = null
)

data class CheckoutRequest(
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("shipping_address") val shippingAddress: String,
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("coupon_code") val couponCode: String? = null,
    @SerializedName("cart") val cart: List<CartItemRequest>
)

data class CheckoutManualRequest(
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("shipping_address") val shippingAddress: String,
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("cart") val cart: List<CartItemRequest>
)

data class CartItemRequest(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Double,
    @SerializedName("quantity") val quantity: Int
)

data class CheckoutResponse(
    @SerializedName("order_id") val orderId: Int,
    @SerializedName("order_code") val orderCode: String,
    @SerializedName("snap_token") val snapToken: String? = null,
    @SerializedName("total_amount") val totalAmount: Double,
    @SerializedName("discount") val discount: Double? = null,
    @SerializedName("payment_method") val paymentMethod: String? = null,
    @SerializedName("unique_code") val uniqueCode: Int? = null,
    @SerializedName("unique_amount") val uniqueAmount: Double? = null
)
