package com.sweetcup.app.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object ProductDetail : Screen("product/{slug}") {
        fun createRoute(slug: String) = "product/$slug"
    }
    object Cart : Screen("cart")
    object Checkout : Screen("checkout")
    object Orders : Screen("orders")
    object OrderDetail : Screen("order/{orderId}") {
        fun createRoute(orderId: Int) = "order/$orderId"
    }
    object Wishlist : Screen("wishlist")
    object Profile : Screen("profile")
    object PaymentMethod : Screen("payment_method")
    object PaymentResult : Screen("payment_result/{orderId}") {
        fun createRoute(orderId: Int) = "payment_result/$orderId"
    }
}
