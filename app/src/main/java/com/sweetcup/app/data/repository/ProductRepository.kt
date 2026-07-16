package com.sweetcup.app.data.repository

import com.sweetcup.app.data.api.RetrofitClient
import com.sweetcup.app.data.model.*

class ProductRepository {

    suspend fun getProducts(
        search: String? = null,
        category: Int? = null,
        sort: String? = null,
        page: Int? = null
    ): Result<PaginatedData<Product>> {
        return try {
            val response = RetrofitClient.api.getProducts(search, category, sort, 12, page)
            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Gagal mengambil produk"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductDetail(slug: String): Result<Product> {
        return try {
            val response = RetrofitClient.api.getProductDetail(slug)
            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Gagal mengambil detail produk"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCategories(): Result<List<Category>> {
        return try {
            val response = RetrofitClient.api.getCategories()
            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Gagal mengambil kategori"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitReview(request: ReviewRequest): Result<Unit> {
        return try {
            val response = RetrofitClient.api.submitReview(request)
            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal mengirim review"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteReview(id: Int): Result<Unit> {
        return try {
            val response = RetrofitClient.api.deleteReview(id)
            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Gagal menghapus review"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
