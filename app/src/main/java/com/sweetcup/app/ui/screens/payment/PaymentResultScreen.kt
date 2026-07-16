package com.sweetcup.app.ui.screens.payment

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
fun PaymentResultScreen(navController: NavController) {
    val orderId = navController.currentBackStackEntry?.arguments?.getInt("orderId") ?: 0
    var order by remember { mutableStateOf<Order?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val repository = remember { OrderRepository() }

    LaunchedEffect(orderId) {
        val result = repository.getOrderDetail(orderId)
        result.onSuccess { order = it }
        isLoading = false
    }

    Scaffold { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Rose500)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Green500
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Pesanan Berhasil!", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Kode Pesanan:", fontSize = 14.sp, color = Gray500)
                Text(order?.orderCode ?: "-", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Rose600)
                Spacer(modifier = Modifier.height(16.dp))

                order?.let { o ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Bayar", color = Gray600)
                                Text(formatCurrency(o.uniqueAmount ?: o.totalAmount), fontWeight = FontWeight.Bold, color = Rose600)
                            }
                            if (o.uniqueCode != null) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Kode Unik", color = Gray600)
                                    Text("${o.uniqueCode}", fontWeight = FontWeight.Bold)
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Metode", color = Gray600)
                                Text(o.paymentMethod?.uppercase() ?: "-")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        navController.navigate(Screen.Orders.route) {
                            popUpTo(Screen.Home.route)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Rose500)
                ) {
                    Text("Lihat Pesanan Saya", fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Kembali ke Beranda")
                }
            }
        }
    }
}
