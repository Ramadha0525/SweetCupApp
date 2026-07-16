package com.sweetcup.app.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.sweetcup.app.data.repository.CartRepository
import com.sweetcup.app.ui.screens.splash.SplashScreen
import com.sweetcup.app.ui.screens.auth.LoginScreen
import com.sweetcup.app.ui.screens.auth.RegisterScreen
import com.sweetcup.app.ui.screens.home.HomeScreen
import com.sweetcup.app.ui.screens.product.ProductDetailScreen
import com.sweetcup.app.ui.screens.cart.CartScreen
import com.sweetcup.app.ui.screens.checkout.CheckoutScreen
import com.sweetcup.app.ui.screens.orders.OrdersScreen
import com.sweetcup.app.ui.screens.orders.OrderDetailScreen
import com.sweetcup.app.ui.screens.wishlist.WishlistScreen
import com.sweetcup.app.ui.screens.profile.ProfileScreen
import com.sweetcup.app.ui.screens.payment.PaymentMethodScreen
import com.sweetcup.app.ui.screens.payment.PaymentResultScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val cartRepository = remember { CartRepository(context) }
    val cartItems by cartRepository.cartItems.collectAsState(initial = emptyList())
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarScreens = listOf(Screen.Home, Screen.Cart, Screen.Orders, Screen.Wishlist, Screen.Profile)
    val showBottomBar = currentRoute in bottomBarScreens.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val items = listOf(
                        Triple(Screen.Home, Icons.Default.Home, "Beranda"),
                        Triple(Screen.Cart, Icons.Default.ShoppingCart, "Keranjang"),
                        Triple(Screen.Orders, Icons.Default.ListAlt, "Pesanan"),
                        Triple(Screen.Wishlist, Icons.Default.Favorite, "Wishlist"),
                        Triple(Screen.Profile, Icons.Default.Person, "Profil")
                    )

                    items.forEach { (screen, icon, label) ->
                        NavigationBarItem(
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (screen == Screen.Cart && cartItems.isNotEmpty()) {
                                            Badge { Text("${cartItems.sumOf { it.quantity }}") }
                                        }
                                    }
                                ) {
                                    Icon(icon, contentDescription = label)
                                }
                            },
                            label = { Text(label) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(navController = navController)
            }
            composable(Screen.Login.route) {
                LoginScreen(navController = navController)
            }
            composable(Screen.Register.route) {
                RegisterScreen(navController = navController)
            }
            composable(Screen.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(
                Screen.ProductDetail.route,
                arguments = listOf(navArgument("slug") { type = NavType.StringType })
            ) {
                ProductDetailScreen(
                    navController = navController,
                    cartRepository = cartRepository
                )
            }
            composable(Screen.Cart.route) {
                CartScreen(
                    navController = navController,
                    cartRepository = cartRepository
                )
            }
            composable(Screen.Checkout.route) {
                CheckoutScreen(
                    navController = navController,
                    cartRepository = cartRepository
                )
            }
            composable(Screen.Orders.route) {
                OrdersScreen(navController = navController)
            }
            composable(
                Screen.OrderDetail.route,
                arguments = listOf(navArgument("orderId") { type = NavType.IntType })
            ) {
                OrderDetailScreen(navController = navController)
            }
            composable(Screen.Wishlist.route) {
                WishlistScreen(
                    navController = navController,
                    cartRepository = cartRepository
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(navController = navController)
            }
            composable(Screen.PaymentMethod.route) {
                PaymentMethodScreen(navController = navController)
            }
            composable(
                Screen.PaymentResult.route,
                arguments = listOf(navArgument("orderId") { type = NavType.IntType })
            ) {
                PaymentResultScreen(navController = navController)
            }
        }
    }
}
