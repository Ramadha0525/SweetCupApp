package com.sweetcup.app.ui.screens.home

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sweetcup.app.data.model.Category
import com.sweetcup.app.data.model.Product
import com.sweetcup.app.data.repository.ProductRepository
import com.sweetcup.app.data.repository.WishlistRepository
import com.sweetcup.app.ui.navigation.Screen
import com.sweetcup.app.ui.theme.*
import com.sweetcup.app.ui.components.ProductCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Int?>(null) }
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var currentPage by remember { mutableIntStateOf(1) }
    var hasMore by remember { mutableStateOf(true) }

    val productRepository = remember { ProductRepository() }

    LaunchedEffect(Unit) {
        val catResult = productRepository.getCategories()
        catResult.onSuccess { categories = it }
    }

    LaunchedEffect(selectedCategory, searchQuery, currentPage) {
        isLoading = true
        val result = productRepository.getProducts(
            search = searchQuery.ifBlank { null },
            category = selectedCategory,
            page = currentPage
        )
        result.onSuccess { paginatedData ->
            products = if (currentPage == 1) paginatedData.data else products + paginatedData.data
            hasMore = paginatedData.data.isNotEmpty()
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🍦", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SweetCup", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Rose500,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(padding)
        ) {
            item(span = { GridItemSpan(2) }) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        currentPage = 1
                    },
                    placeholder = { Text("Cari produk...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(25.dp),
                    singleLine = true
                )
            }

            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = {
                            selectedCategory = null
                            currentPage = 1
                        },
                        label = { Text("✨ Semua") }
                    )
                    categories.forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category.id,
                            onClick = {
                                selectedCategory = category.id
                                currentPage = 1
                            },
                            label = { Text("${category.icon} ${category.name}") }
                        )
                    }
                }
            }

            items(products) { product ->
                ProductCard(
                    product = product,
                    onClick = {
                        navController.navigate(Screen.ProductDetail.createRoute(product.slug))
                    }
                )
            }

            if (isLoading) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Rose500)
                    }
                }
            }

            if (hasMore && !isLoading && products.isNotEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    TextButton(
                        onClick = { currentPage++ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Muat Lebih Banyak", color = Rose500)
                    }
                }
            }
        }
    }
}
