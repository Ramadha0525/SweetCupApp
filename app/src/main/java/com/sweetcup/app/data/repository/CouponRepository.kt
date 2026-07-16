package com.sweetcup.app.data.repository

import com.sweetcup.app.data.api.RetrofitClient
import com.sweetcup.app.data.model.*

class CouponRepository {

    suspend fun applyCoupon(code: String): Result<Coupon> {
        return try {
            val response = RetrofitClient.api.applyCoupon(CouponApplyRequest(code))
            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Kupon tidak valid"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
