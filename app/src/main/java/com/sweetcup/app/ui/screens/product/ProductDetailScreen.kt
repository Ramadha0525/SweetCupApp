package com.sweetcup.app.ui.screens.product

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.sweetcup.app.data.model.CartItem
import com.sweetcup.app.data.model.Product
import com.sweetcup.app.data.model.ReviewRequest
import com.sweetcup.app.data.repository.CartRepository
import com.sweetcup.app.data.repository.ProductRepository
import com.sweetcup.app.ui.components.formatCurrency
import com.sweetcup.app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    cartRepository: CartRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { ProductRepository() }
    val slug = navController.currentBackStackEntry?.arguments?.getString("slug") ?: ""

    var product by remember { mutableStateOf<Product?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var reviewRating by remember { mutableIntStateOf(5) }
    var reviewComment by remember { mutableStateOf("") }
    var isSubmittingReview by remember { mutableStateOf(false) }

    LaunchedEffect(slug) {
        val result = repository.getProductDetail(slug)
        result.onSuccess { product = it }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product?.name ?: "Detail Produk") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Rose500,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = Color.White) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatCurrency(product?.price ?: 0.0),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Rose600
                    )
                    Button(
                        onClick = {
                            product?.let { p ->
                                scope.launch {
                                    cartRepository.addItem(
                                        CartItem(
                                            id = p.id,
                                            name = p.name,
                                            price = p.price,
                                            image = p.image
                                        )
                                    )
                                    Toast.makeText(context, "Ditambahkan ke keranjang", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Rose500),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tambah ke Keranjang", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Rose500)
            }
        } else {
            product?.let { p ->
                LazyColumn(
                    modifier = Modifier.padding(padding),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    item {
                        if (p.image != null) {
                            AsyncImage(
                                model = p.image,
                                contentDescription = p.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🍦", fontSize = 80.sp)
                            }
                        }
                    }

                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = p.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Amber400,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "${p.averageRating ?: 0.0}",
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                                Text(
                                    text = " (${p.reviewsCount ?: 0} ulasan)",
                                    fontSize = 13.sp,
                                    color = Gray500
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = formatCurrency(p.price),
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = Rose600
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Deskripsi",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = p.description ?: "Tidak ada deskripsi",
                                fontSize = 14.sp,
                                color = Gray600,
                                lineHeight = 22.sp
                            )
                        }
                    }

                    item {
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Tulis Review",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            (1..5).forEach { star ->
                                IconButton(onClick = { reviewRating = star }) {
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (star <= reviewRating) Amber400 else Gray300,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                        OutlinedTextField(
                            value = reviewComment,
                            onValueChange = { reviewComment = it },
                            label = { Text("Komentar (opsional)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            minLines = 2
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                isSubmittingReview = true
                                scope.launch {
                                    val result = repository.submitReview(
                                        ReviewRequest(p.id, reviewRating, reviewComment.ifBlank { null })
                                    )
                                    result.onSuccess {
                                        Toast.makeText(context, "Review berhasil dikirim", Toast.LENGTH_SHORT).show()
                                        reviewComment = ""
                                        reviewRating = 5
                                        val refreshed = repository.getProductDetail(slug)
                                        refreshed.onSuccess { product = it }
                                    }.onFailure {
                                        Toast.makeText(context, "Gagal mengirim review", Toast.LENGTH_SHORT).show()
                                    }
                                    isSubmittingReview = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Rose500),
                            enabled = !isSubmittingReview
                        ) {
                            Text("Kirim Review")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (!p.reviews.isNullOrEmpty()) {
                        item {
                            Text(
                                text = "Ulasan (${p.reviews?.size ?: 0})",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        items(p.reviews ?: emptyList()) { review ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = review.user?.name ?: "User",
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Row {
                                            (1..5).forEach { star ->
                                                Icon(
                                                    Icons.Default.Star,
                                                    contentDescription = null,
                                                    tint = if (star <= review.rating) Amber400 else Gray300,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }
                                    }
                                    if (!review.comment.isNullOrBlank()) {
                                        Text(
                                            text = review.comment!!,
                                            fontSize = 13.sp,
                                            color = Gray600,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
