package com.sweetcup.app.ui.screens.orders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.sweetcup.app.ui.navigation.Screen
import com.sweetcup.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(navController: NavController) {
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedFilter by remember { mutableStateOf<String?>(null) }

    val repository = remember { OrderRepository() }

    LaunchedEffect(selectedFilter) {
        isLoading = true
        val result = repository.getOrders(selectedFilter)
        result.onSuccess { orders = it.data }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pesanan Saya") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Rose500,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val filters = listOf(null to "Semua", "pending" to "Menunggu", "paid" to "Lunas", "processing" to "Diproses", "shipped" to "Dikirim", "delivered" to "Selesai")
                filters.forEach { (status, label) ->
                    FilterChip(
                        selected = selectedFilter == status,
                        onClick = { selectedFilter = status },
                        label = { Text(label, fontSize = 11.sp) }
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Rose500)
                }
            } else if (orders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📦", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Belum ada pesanan", color = Gray500)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(orders) { order ->
                        OrderCard(order = order) {
                            navController.navigate(Screen.OrderDetail.createRoute(order.id))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderCard(order: Order, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(order.orderCode, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                StatusBadge(order.status)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(order.paymentMethod?.uppercase() ?: "-", fontSize = 12.sp, color = Gray500)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(order.createdAt ?: "", fontSize = 12.sp, color = Gray400)
                Text(
                    formatCurrency(order.uniqueAmount ?: order.totalAmount),
                    fontWeight = FontWeight.Bold,
                    color = Rose600,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (label, color) = when (status) {
        "pending" -> "Menunggu" to Yellow500
        "paid" -> "Lunas" to Blue500
        "processing" -> "Diproses" to Purple500
        "shipped" -> "Dikirim" to Indigo500
        "delivered" -> "Diterima" to Green500
        "cancelled" -> "Dibatalkan" to Red500
        else -> status to Gray400
    }
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}
