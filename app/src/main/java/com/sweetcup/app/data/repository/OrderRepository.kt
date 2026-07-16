package com.sweetcup.app.data.repository

import com.sweetcup.app.data.api.RetrofitClient
import com.sweetcup.app.data.model.*

class OrderRepository {

    suspend fun checkout(request: CheckoutRequest): Result<CheckoutResponse> {
        return try {
            val response = RetrofitClient.api.checkout(request)
            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Checkout gagal"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkoutManual(request: CheckoutManualRequest): Result<CheckoutResponse> {
        return try {
            val response = RetrofitClient.api.checkoutManual(request)
            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Checkout gagal"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrders(status: String? = null): Result<PaginatedData<Order>> {
        return try {
            val response = RetrofitClient.api.getOrders(status)
            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Gagal mengambil pesanan"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrderDetail(id: Int): Result<Order> {
        return try {
            val response = RetrofitClient.api.getOrderDetail(id)
            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Gagal mengambil detail pesanan"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
