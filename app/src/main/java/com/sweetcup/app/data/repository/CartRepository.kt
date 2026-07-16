package com.sweetcup.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.sweetcup.app.data.model.CartItem
import com.sweetcup.app.data.model.Coupon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import android.content.SharedPreferences

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cart")

class CartRepository(private val context: Context) {

    private val cartKey = stringPreferencesKey("cart_items")
    private val couponKey = stringPreferencesKey("applied_coupon")

    val cartItems: Flow<List<CartItem>> = context.dataStore.data.map { prefs ->
        val json = prefs[cartKey] ?: "[]"
        try {
            Json.decodeFromString<List<CartItem>>(json)
        } catch (e: Exception) {
            emptyList()
        }
    }

    val appliedCoupon: Flow<Coupon?> = context.dataStore.data.map { prefs ->
        val json = prefs[couponKey] ?: return@map null
        try {
            Json.decodeFromString<Coupon>(json)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addItem(item: CartItem) {
        context.dataStore.edit { prefs ->
            val current = getCartFromPrefs(prefs)
            val existing = current.find { it.id == item.id }
            val updated = if (existing != null) {
                current.map { if (it.id == item.id) it.copy(quantity = it.quantity + item.quantity) else it }
            } else {
                current + item
            }
            prefs[cartKey] = Json.encodeToString(updated)
        }
    }

    suspend fun updateQuantity(itemId: Int, quantity: Int) {
        context.dataStore.edit { prefs ->
            val current = getCartFromPrefs(prefs)
            val updated = if (quantity <= 0) {
                current.filter { it.id != itemId }
            } else {
                current.map { if (it.id == itemId) it.copy(quantity = quantity) else it }
            }
            prefs[cartKey] = Json.encodeToString(updated)
        }
    }

    suspend fun removeItem(itemId: Int) {
        context.dataStore.edit { prefs ->
            val current = getCartFromPrefs(prefs)
            prefs[cartKey] = Json.encodeToString(current.filter { it.id != itemId })
        }
    }

    suspend fun clearCart() {
        context.dataStore.edit { prefs ->
            prefs[cartKey] = "[]"
            prefs.remove(couponKey)
        }
    }

    suspend fun applyCoupon(coupon: Coupon) {
        context.dataStore.edit { prefs ->
            prefs[couponKey] = Json.encodeToString(coupon)
        }
    }

    suspend fun removeCoupon() {
        context.dataStore.edit { prefs ->
            prefs.remove(couponKey)
        }
    }

    private fun getCartFromPrefs(prefs: Preferences): List<CartItem> {
        val json = prefs[cartKey] ?: "[]"
        return try {
            Json.decodeFromString<List<CartItem>>(json)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
