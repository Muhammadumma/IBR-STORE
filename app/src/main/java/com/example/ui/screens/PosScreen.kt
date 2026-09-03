package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CartItem
import com.example.data.model.Product
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.FintechEmerald
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.VendoraViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun PosScreen(viewModel: VendoraViewModel) {
    val products by viewModel.products.collectAsStateWithLifecycle()
    val cart by viewModel.cart.collectAsStateWithLifecycle()
    val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf("Cash") }
    var showDebtDetailsDialog by remember { mutableStateOf(false) }

    // Filter products
    val filteredProducts = remember(products, searchQuery) {
        if (searchQuery.isBlank()) products
        else products.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.category.contains(searchQuery, ignoreCase = true)
        }
    }

    val subtotal = remember(cart) { cart.sumOf { it.price * it.qty } }
    val vatRate = if (appSettings?.vatEnabled == true) appSettings?.vatRate ?: 0.0 else 0.0
    val vat = remember(subtotal, vatRate) { if (vatRate > 0) subtotal * (vatRate / 100.0) else 0.0 }
    val total = subtotal + vat
    val expectedProfit = remember(cart) { cart.sumOf { (it.price - it.buyPrice) * it.qty } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Search Bar (Padding: 16dp horizontal, 12dp top, 8dp bottom)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        "Search items or categories...",
                        fontSize = 14.sp,
                        color = TextMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = FintechEmerald,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pos_search_input")
            )
        }

        // Split View: Products 50% width grid on top, Cart Card on bottom
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Product Grid: Exactly 50% width (2-column) on mobile with 12dp spacing
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredProducts, key = { it.id }) { product ->
                    val isOutOfStock = product.qty <= 0
                    val inCartItem = cart.find { it.productId == product.id }
                    val currentInCartQty = inCartItem?.qty ?: 0

                    PosProductCard(
                        product = product,
                        inCartQty = currentInCartQty,
                        isOutOfStock = isOutOfStock,
                        viewModel = viewModel,
                        onClick = {
                            if (!isOutOfStock) {
                                viewModel.addToCart(product)
                            }
                        }
                    )
                }
            }

            // Current Sale Cart Container
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                shadowElevation = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    // Header Row: Title, Profit pill, and Clear button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Current Sale",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (cart.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = FintechEmerald.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "${cart.sumOf { it.qty }} items",
                                        color = FintechEmerald,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (cart.isNotEmpty() && expectedProfit > 0) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = SuccessGreen.copy(alpha = 0.1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.TrendingUp,
                                            contentDescription = null,
                                            tint = SuccessGreen,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "+${viewModel.formatCurrency(expectedProfit)}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SuccessGreen
                                        )
                                    }
                                }
                            }

                            if (cart.isNotEmpty()) {
                                TextButton(
                                    onClick = { viewModel.clearCart() },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        "Clear",
                                        color = TextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Cart Items: Dedicated scrollable list container with comfortable height
                    if (cart.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Tap any product above to add to cart",
                                fontSize = 13.sp,
                                color = TextMuted
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((cart.size * 56).coerceIn(60, 140).dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(cart, key = { it.productId }) { item ->
                                val prod = products.find { it.id == item.productId }
                                val maxStock = prod?.qty ?: item.qty

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                            Text(
                                                text = item.name,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "${viewModel.formatCurrency(item.price)} each • Total ${viewModel.formatCurrency(item.price * item.qty)}",
                                                fontSize = 11.sp,
                                                color = TextSecondary
                                            )
                                        }

                                        // Clean, round minimal quantity icons (+ / -)
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Surface(
                                                shape = CircleShape,
                                                color = MaterialTheme.colorScheme.surface,
                                                shadowElevation = 1.dp,
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clickable {
                                                        viewModel.updateCartItemQty(item.productId, item.qty - 1, maxStock)
                                                    }
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        Icons.Default.Remove,
                                                        contentDescription = "Decrease",
                                                        modifier = Modifier.size(16.dp),
                                                        tint = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                            }

                                            Text(
                                                text = "${item.qty}",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.padding(horizontal = 4.dp)
                                            )

                                            Surface(
                                                shape = CircleShape,
                                                color = MaterialTheme.colorScheme.surface,
                                                shadowElevation = 1.dp,
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clickable {
                                                        viewModel.updateCartItemQty(item.productId, item.qty + 1, maxStock)
                                                    }
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        Icons.Default.Add,
                                                        contentDescription = "Increase",
                                                        modifier = Modifier.size(16.dp),
                                                        tint = FintechEmerald
                                                    )
                                                }
                                            }

                                            IconButton(
                                                onClick = { viewModel.removeFromCart(item.productId) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Delete",
                                                    tint = DangerRed.copy(alpha = 0.8f),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Payment Method Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PaymentMethodPill(
                            title = "Cash",
                            icon = Icons.Default.Payments,
                            isSelected = selectedPaymentMethod == "Cash",
                            onClick = { selectedPaymentMethod = "Cash" },
                            modifier = Modifier.weight(1f)
                        )
                        PaymentMethodPill(
                            title = "Bank",
                            icon = Icons.Default.AccountBalance,
                            isSelected = selectedPaymentMethod == "Store Bank",
                            onClick = { selectedPaymentMethod = "Store Bank" },
                            modifier = Modifier.weight(1f)
                        )
                        PaymentMethodPill(
                            title = "POS",
                            icon = Icons.Default.CreditCard,
                            isSelected = selectedPaymentMethod == "POS",
                            onClick = { selectedPaymentMethod = "POS" },
                            modifier = Modifier.weight(1f)
                        )
                        PaymentMethodPill(
                            title = "Debt",
                            icon = Icons.Default.MoneyOff,
                            isSelected = selectedPaymentMethod == "Debt",
                            onClick = { selectedPaymentMethod = "Debt" },
                            isWarning = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Subtotal / VAT info
                    if (vat > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subtotal", fontSize = 12.sp, color = TextSecondary)
                            Text(viewModel.formatCurrency(subtotal), fontSize = 12.sp, color = TextSecondary)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("VAT (${vatRate}%)", fontSize = 12.sp, color = TextSecondary)
                            Text(viewModel.formatCurrency(vat), fontSize = 12.sp, color = TextSecondary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    // PRIMARY CTA: Large, full-width, high-contrast Confirm Sale button
                    Button(
                        onClick = {
                            if (cart.isEmpty()) return@Button
                            if (selectedPaymentMethod == "Debt") {
                                showDebtDetailsDialog = true
                            } else {
                                viewModel.checkout(
                                    paymentMethod = selectedPaymentMethod,
                                    customerName = "Walk-in Customer",
                                    customerPhone = "",
                                    dueDate = null,
                                    onSuccess = {}
                                )
                            }
                        },
                        enabled = cart.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FintechEmerald,
                            contentColor = Color.White,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContentColor = TextMuted
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("confirm_sale_btn")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (cart.isNotEmpty()) "Confirm Sale" else "Cart is Empty",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (cart.isNotEmpty()) {
                                Text(
                                    text = viewModel.formatCurrency(total),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Debt Customer Details Dialog
    if (showDebtDetailsDialog) {
        var debtorName by remember { mutableStateOf("") }
        var debtorPhone by remember { mutableStateOf("") }
        val defaultDueDate = remember {
            val c = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 7) }
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(c.time)
        }
        var dueDate by remember { mutableStateOf(defaultDueDate) }

        AlertDialog(
            onDismissRequest = { showDebtDetailsDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = WarningAmber) },
            title = { Text("Debt Record Details", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Record debtor details for recovery tracking.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = debtorName,
                        onValueChange = { debtorName = it },
                        label = { Text("Debtor Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = debtorPhone,
                        onValueChange = { debtorPhone = it },
                        label = { Text("Phone Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = dueDate,
                        onValueChange = { dueDate = it },
                        label = { Text("Due Date (YYYY-MM-DD)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (debtorName.isBlank()) {
                            viewModel.showToast("Debtor Name is required.")
                            return@Button
                        }
                        showDebtDetailsDialog = false
                        viewModel.checkout(
                            paymentMethod = "Debt",
                            customerName = debtorName.trim(),
                            customerPhone = debtorPhone.trim(),
                            dueDate = dueDate.trim(),
                            onSuccess = {}
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WarningAmber)
                ) {
                    Text("Record Debt Sale", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDebtDetailsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun PosProductCard(
    product: Product,
    inCartQty: Int,
    isOutOfStock: Boolean,
    viewModel: VendoraViewModel,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (inCartQty > 0) FintechEmerald else MaterialTheme.colorScheme.outline
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isOutOfStock) { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Top Row: Category icon & in-cart badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "📦", fontSize = 18.sp)
                    }
                }

                if (inCartQty > 0) {
                    Surface(
                        shape = CircleShape,
                        color = FintechEmerald,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "$inCartQty",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Product Name: Bold typography, clean hierarchy
            Text(
                text = product.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isOutOfStock) TextMuted else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Price: High contrast Deep Charcoal
            Text(
                text = viewModel.formatCurrency(product.sellPrice),
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = if (isOutOfStock) TextMuted else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Stock Indicator: Sleek, minimalist
            Text(
                text = if (isOutOfStock) "Out of Stock" else "${product.qty} left",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = when {
                    isOutOfStock -> DangerRed
                    product.qty <= 5 -> WarningAmber
                    else -> TextSecondary
                }
            )
        }
    }
}

@Composable
fun PaymentMethodPill(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    isWarning: Boolean = false,
    modifier: Modifier = Modifier
) {
    val activeColor = if (isWarning) WarningAmber else FintechEmerald
    val bgColor = if (isSelected) activeColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = bgColor,
        border = if (isSelected) BorderStroke(1.5.dp, activeColor) else null,
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) activeColor else TextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
