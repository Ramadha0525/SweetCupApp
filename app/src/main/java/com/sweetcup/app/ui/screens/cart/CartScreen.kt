package com.sweetcup.app.ui.screens.cart

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sweetcup.app.data.model.CartItem
import com.sweetcup.app.data.repository.CartRepository
import com.sweetcup.app.data.repository.CouponRepository
import com.sweetcup.app.ui.components.formatCurrency
import com.sweetcup.app.ui.navigation.Screen
import com.sweetcup.app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    cartRepository: CartRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cartItems by cartRepository.cartItems.collectAsState(initial = emptyList())
    val appliedCoupon by cartRepository.appliedCoupon.collectAsState(initial = null)
    var couponCode by remember { mutableStateOf("") }
    var isApplyingCoupon by remember { mutableStateOf(false) }

    val couponRepository = remember { CouponRepository() }
    val shippingCost = 20000.0

    val subtotal = cartItems.sumOf { it.subtotal }
    val discount = appliedCoupon?.let { coupon ->
        when (coupon.type) {
            "percentage" -> (subtotal * coupon.value / 100).coerceAtMost(subtotal)
            "fixed" -> coupon.value.coerceAtMost(subtotal)
            else -> 0.0
        }
    } ?: 0.0
    val total = subtotal - discount + shippingCost

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Keranjang") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Rose500,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🛒", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Keranjang kosong", fontSize = 16.sp, color = Gray500)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { navController.navigate(Screen.Home.route) }) {
                        Text("Mulai Belanja", color = Rose500)
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cartItems) { item ->
                        CartItemRow(
                            item = item,
                            onQuantityChange = { newQty ->
                                scope.launch { cartRepository.updateQuantity(item.id, newQty) }
                            },
                            onRemove = {
                                scope.launch { cartRepository.removeItem(item.id) }
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = couponCode,
                                onValueChange = { couponCode = it },
                                label = { Text("Kode Kupon") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (couponCode.isBlank()) return@Button
                                    isApplyingCoupon = true
                                    scope.launch {
                                        val result = couponRepository.applyCoupon(couponCode)
                                        result.onSuccess { coupon ->
                                            cartRepository.applyCoupon(coupon)
                                            Toast.makeText(context, "Kupon diterapkan!", Toast.LENGTH_SHORT).show()
                                        }.onFailure { e ->
                                            Toast.makeText(context, e.message ?: "Kupon tidak valid", Toast.LENGTH_SHORT).show()
                                        }
                                        isApplyingCoupon = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Rose500),
                                enabled = !isApplyingCoupon
                            ) {
                                Text("Terapkan")
                            }
                        }
                    }

                    item {
                        appliedCoupon?.let { coupon ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Green500.copy(alpha = 0.1f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.LocalOffer, contentDescription = null, tint = Green500)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Kupon: ${coupon.code} (-${formatCurrency(discount)})",
                                        color = Green500,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    IconButton(onClick = {
                                        scope.launch { cartRepository.removeCoupon() }
                                        couponCode = ""
                                    }) {
                                        Icon(Icons.Default.Close, contentDescription = "Hapus", tint = Gray500)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                SummaryRow("Subtotal", formatCurrency(subtotal))
                                SummaryRow("Ongkir", formatCurrency(shippingCost))
                                if (discount > 0) {
                                    SummaryRow("Diskon", "-${formatCurrency(discount)}", color = Green500)
                                }
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                SummaryRow("Total", formatCurrency(total), isBold = true, color = Rose600)
                            }
                        }
                    }
                }

                Button(
                    onClick = { navController.navigate(Screen.Checkout.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Rose500)
                ) {
                    Text("Lanjut ke Checkout", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🍦", fontSize = 40.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text(formatCurrency(item.price), color = Rose600, fontSize = 13.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onQuantityChange(item.quantity - 1) }) {
                    Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(18.dp))
                }
                Text("${item.quantity}", fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 8.dp))
                IconButton(onClick = { onQuantityChange(item.quantity + 1) }) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Red500, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    color: Color = Gray900
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = Gray600
        )
        Text(
            value,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = color
        )
    }
}
