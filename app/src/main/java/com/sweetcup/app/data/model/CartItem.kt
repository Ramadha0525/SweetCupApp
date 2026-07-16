package com.sweetcup.app.data.model

data class CartItem(
    val id: Int,
    val name: String,
    val price: Double,
    val image: String? = null,
    val quantity: Int = 1
) {
    val subtotal: Double get() = price * quantity
}
