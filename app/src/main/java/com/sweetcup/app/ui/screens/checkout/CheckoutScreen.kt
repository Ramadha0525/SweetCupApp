package com.sweetcup.app.ui.screens.checkout

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import com.sweetcup.app.data.model.*
import com.sweetcup.app.data.repository.CartRepository
import com.sweetcup.app.data.repository.OrderRepository
import com.sweetcup.app.data.repository.SettingsRepository
import com.sweetcup.app.ui.components.formatCurrency
import com.sweetcup.app.ui.navigation.Screen
import com.sweetcup.app.ui.theme.*
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    cartRepository: CartRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cartItems by cartRepository.cartItems.collectAsState(initial = emptyList())
    val appliedCoupon by cartRepository.appliedCoupon.collectAsState(initial = null)
    val orderRepository = remember { OrderRepository() }
    val settingsRepository = remember { SettingsRepository() }

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedPayment by remember { mutableStateOf("cod") }
    var isLoading by remember { mutableStateOf(false) }
    var paymentSettings by remember { mutableStateOf<PaymentSettings?>(null) }

    LaunchedEffect(Unit) {
        val prefs = context.dataStore.data.first()
        name = prefs[stringPreferencesKey("user_name")] ?: ""
        phone = prefs[stringPreferencesKey("user_phone")] ?: ""
        address = prefs[stringPreferencesKey("user_address")] ?: ""

        val result = settingsRepository.getPaymentSettings()
        result.onSuccess { paymentSettings = it }
    }

    val shippingCost = paymentSettings?.shippingCost?.toDoubleOrNull() ?: 20000.0
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
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Informasi Pengiriman", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Telepon") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Alamat Pengiriman") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Catatan (opsional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(20.dp))

            Text("Metode Pembayaran", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (paymentSettings?.codEnabled == "1") {
                    PaymentOption(
                        title = "Bayar di Tempat (COD)",
                        subtitle = "Bayar saat barang diterima",
                        icon = "💵",
                        selected = selectedPayment == "cod",
                        onClick = { selectedPayment = "cod" }
                    )
                }
                if (paymentSettings?.ewalletEnabled == "1") {
                    PaymentOption(
                        title = "E-Wallet",
                        subtitle = "${paymentSettings?.ewalletName ?: "GoPay"} - ${paymentSettings?.ewalletNumber ?: ""}",
                        icon = "📱",
                        selected = selectedPayment == "ewallet",
                        onClick = { selectedPayment = "ewallet" }
                    )
                }
                if (paymentSettings?.qrisEnabled == "1") {
                    PaymentOption(
                        title = "QRIS",
                        subtitle = "Scan QR Code untuk bayar",
                        icon = "📷",
                        selected = selectedPayment == "qris",
                        onClick = { selectedPayment = "qris" }
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            Text("Ringkasan Pesanan", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    cartItems.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${item.name} x${item.quantity}", fontSize = 13.sp, color = Gray600)
                            Text(formatCurrency(item.subtotal), fontSize = 13.sp)
                        }
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    SummaryRow("Subtotal", formatCurrency(subtotal))
                    SummaryRow("Ongkir", formatCurrency(shippingCost))
                    if (discount > 0) SummaryRow("Diskon", "-${formatCurrency(discount)}", color = Green500)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    SummaryRow("Total", formatCurrency(total), isBold = true, color = Rose600)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (name.isBlank() || phone.isBlank() || address.isBlank()) {
                        Toast.makeText(context, "Lengkapi data pengiriman", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isLoading = true
                    scope.launch {
                        val request = CheckoutManualRequest(
                            name = name,
                            phone = phone,
                            shippingAddress = address,
                            notes = notes.ifBlank { null },
                            paymentMethod = selectedPayment,
                            cart = cartItems.map {
                                CartItemRequest(it.id, it.name, it.price, it.quantity)
                            }
                        )
                        val result = orderRepository.checkoutManual(request)
                        result.onSuccess { response ->
                            cartRepository.clearCart()
                            navController.navigate(Screen.PaymentResult.createRoute(response.orderId)) {
                                popUpTo(Screen.Home.route)
                            }
                        }.onFailure { e ->
                            Toast.makeText(context, e.message ?: "Checkout gagal", Toast.LENGTH_SHORT).show()
                        }
                        isLoading = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Rose500),
                enabled = !isLoading && cartItems.isNotEmpty()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Buat Pesanan", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PaymentOption(
    title: String,
    subtitle: String,
    icon: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Rose50.copy(alpha = 0.5f) else Color.White
        ),
        border = if (selected) CardDefaults.outlinedCardBorder() else null
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 28.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Medium)
                Text(subtitle, fontSize = 12.sp, color = Gray500)
            }
            RadioButton(selected = selected, onClick = onClick, colors = RadioButtonDefaults.colors(selectedColor = Rose500))
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, isBold: Boolean = false, color: Color = Gray900) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal, color = Gray600, fontSize = 13.sp)
        Text(value, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal, color = color, fontSize = 13.sp)
    }
}
