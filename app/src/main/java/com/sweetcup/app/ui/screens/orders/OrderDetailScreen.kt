package com.sweetcup.app.ui.screens.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sweetcup.app.data.model.Order
import com.sweetcup.app.data.repository.OrderRepository
import com.sweetcup.app.ui.components.formatCurrency
import com.sweetcup.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(navController: NavController) {
    val orderId = navController.currentBackStackEntry?.arguments?.getInt("orderId") ?: 0
    var order by remember { mutableStateOf<Order?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val repository = remember { OrderRepository() }

    LaunchedEffect(orderId) {
        val result = repository.getOrderDetail(orderId)
        result.onSuccess { order = it }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Pesanan") },
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
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Rose500)
            }
        } else {
            order?.let { o ->
                LazyColumn(
                    modifier = Modifier.padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(o.orderCode, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    StatusBadge(o.status)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(o.createdAt ?: "", fontSize = 12.sp, color = Gray400)
                            }
                        }
                    }

                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Informasi Pengiriman", fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Nama: ${o.shippingAddress?.substringBefore("\n") ?: "-"}", fontSize = 13.sp)
                                Text("Alamat: ${o.shippingAddress ?: "-"}", fontSize = 13.sp, color = Gray600)
                                if (!o.trackingNumber.isNullOrBlank()) {
                                    Text("Resi: ${o.trackingNumber}", fontSize = 13.sp, color = Blue500)
                                }
                            }
                        }
                    }

                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Informasi Pembayaran", fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Metode: ${o.paymentMethod?.uppercase() ?: "-"}", fontSize = 13.sp)
                                if (o.uniqueCode != null) {
                                    Text("Kode Unik: ${o.uniqueCode}", fontSize = 13.sp, color = Gray600)
                                }
                                Text(
                                    "Total: ${formatCurrency(o.uniqueAmount ?: o.totalAmount)}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Rose600
                                )
                            }
                        }
                    }

                    item {
                        Text("Item Pesanan", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    }

                    o.items?.let { items ->
                        items(items) { item ->
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text("🍦", fontSize = 32.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.productName, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                        Text("x${item.quantity} @ ${formatCurrency(item.price)}", fontSize = 12.sp, color = Gray500)
                                    }
                                    Text(formatCurrency(item.price * item.quantity), fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
