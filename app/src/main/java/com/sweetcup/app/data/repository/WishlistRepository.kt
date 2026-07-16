package com.sweetcup.app.data.repository

import com.sweetcup.app.data.api.RetrofitClient
import com.sweetcup.app.data.model.*

class WishlistRepository {

    suspend fun getWishlist(): Result<PaginatedData<Product>> {
        return try {
            val response = RetrofitClient.api.getWishlist()
            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Gagal mengambil wishlist"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleWishlist(productId: Int): Result<String> {
        return try {
            val response = RetrofitClient.api.toggleWishlist(WishlistRequest(productId))
            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(response.body()!!.data.status)
            } else {
                Result.failure(Exception("Gagal update wishlist"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
