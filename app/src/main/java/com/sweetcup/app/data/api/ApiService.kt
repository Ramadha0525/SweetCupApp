package com.sweetcup.app.data.api

import com.sweetcup.app.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthResponse>>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthResponse>>

    @POST("logout")
    suspend fun logout(): Response<ApiResponse<Any>>

    @GET("user")
    suspend fun getUser(): Response<ApiResponse<User>>

    @GET("profile")
    suspend fun getProfile(): Response<ApiResponse<User>>

    @PUT("profile")
    suspend fun updateProfile(@Body request: ProfileUpdateRequest): Response<ApiResponse<User>>

    @GET("products")
    suspend fun getProducts(
        @Query("search") search: String? = null,
        @Query("category") category: Int? = null,
        @Query("sort") sort: String? = null,
        @Query("per_page") perPage: Int? = null,
        @Query("page") page: Int? = null
    ): Response<ApiResponse<PaginatedData<Product>>>

    @GET("products/{slug}")
    suspend fun getProductDetail(@Path("slug") slug: String): Response<ApiResponse<Product>>

    @GET("categories")
    suspend fun getCategories(): Response<ApiResponse<List<Category>>>

    @POST("checkout")
    suspend fun checkout(@Body request: CheckoutRequest): Response<ApiResponse<CheckoutResponse>>

    @POST("checkout/manual")
    suspend fun checkoutManual(@Body request: CheckoutManualRequest): Response<ApiResponse<CheckoutResponse>>

    @GET("orders")
    suspend fun getOrders(
        @Query("status") status: String? = null
    ): Response<ApiResponse<PaginatedData<Order>>>

    @GET("orders/{id}")
    suspend fun getOrderDetail(@Path("id") id: Int): Response<ApiResponse<Order>>

    @POST("reviews")
    suspend fun submitReview(@Body request: ReviewRequest): Response<ApiResponse<Any>>

    @DELETE("reviews/{id}")
    suspend fun deleteReview(@Path("id") id: Int): Response<ApiResponse<Any>>

    @GET("wishlist")
    suspend fun getWishlist(): Response<ApiResponse<PaginatedData<Product>>>

    @POST("wishlist/toggle")
    suspend fun toggleWishlist(@Body request: WishlistRequest): Response<ApiResponse<WishlistResponse>>

    @POST("coupons/apply")
    suspend fun applyCoupon(@Body request: CouponApplyRequest): Response<ApiResponse<Coupon>>

    @GET("payment-settings")
    suspend fun getPaymentSettings(): Response<ApiResponse<PaymentSettings>>

    @GET("web-settings")
    suspend fun getWebSettings(): Response<ApiResponse<WebSettings>>
}
