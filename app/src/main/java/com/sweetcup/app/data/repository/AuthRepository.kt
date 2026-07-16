package com.sweetcup.app.data.repository

import com.sweetcup.app.data.api.RetrofitClient
import com.sweetcup.app.data.model.*

class AuthRepository {

    suspend fun register(request: RegisterRequest): Result<AuthResponse> {
        return try {
            val response = RetrofitClient.api.register(request)
            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Registrasi gagal"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(request: LoginRequest): Result<AuthResponse> {
        return try {
            val response = RetrofitClient.api.login(request)
            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Login gagal"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            val response = RetrofitClient.api.logout()
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Logout gagal"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProfile(): Result<User> {
        return try {
            val response = RetrofitClient.api.getProfile()
            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Gagal mengambil profil"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(request: ProfileUpdateRequest): Result<User> {
        return try {
            val response = RetrofitClient.api.updateProfile(request)
            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal update profil"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
