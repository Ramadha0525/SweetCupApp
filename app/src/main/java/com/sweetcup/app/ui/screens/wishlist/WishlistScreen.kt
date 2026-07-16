package com.sweetcup.app.ui.screens.wishlist

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sweetcup.app.data.model.CartItem
import com.sweetcup.app.data.model.Product
import com.sweetcup.app.data.repository.CartRepository
import com.sweetcup.app.data.repository.WishlistRepository
import com.sweetcup.app.ui.components.ProductCard
import com.sweetcup.app.ui.navigation.Screen
import com.sweetcup.app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    navController: NavController,
    cartRepository: CartRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val repository = remember { WishlistRepository() }

    LaunchedEffect(Unit) {
        val result = repository.getWishlist()
        result.onSuccess { products = it.data }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wishlist") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Rose500,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Rose500)
            }
        } else if (products.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("❤️", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Wishlist kosong", fontSize = 16.sp, color = Gray500)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(padding)
            ) {
                items(products) { product ->
                    ProductCard(
                        product = product,
                        onClick = {
                            navController.navigate(Screen.ProductDetail.createRoute(product.slug))
                        },
                        onAddToCart = {
                            scope.launch {
                                cartRepository.addItem(
                                    CartItem(product.id, product.name, product.price, product.image)
                                )
                                Toast.makeText(context, "Ditambahkan ke keranjang", Toast.LENGTH_SHORT).show()
                            }
                        },
                        isWishlisted = true,
                        onWishlistToggle = {
                            scope.launch {
                                val result = repository.toggleWishlist(product.id)
                                result.onSuccess {
                                    if (it == "removed") {
                                        products = products.filter { p -> p.id != product.id }
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
