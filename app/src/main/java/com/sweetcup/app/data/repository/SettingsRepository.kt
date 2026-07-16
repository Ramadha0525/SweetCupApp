package com.sweetcup.app.data.repository

import com.sweetcup.app.data.api.RetrofitClient
import com.sweetcup.app.data.model.*

class SettingsRepository {

    suspend fun getPaymentSettings(): Result<PaymentSettings> {
        return try {
            val response = RetrofitClient.api.getPaymentSettings()
            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Gagal mengambil pengaturan pembayaran"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWebSettings(): Result<WebSettings> {
        return try {
            val response = RetrofitClient.api.getWebSettings()
            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Gagal mengambil pengaturan web"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
