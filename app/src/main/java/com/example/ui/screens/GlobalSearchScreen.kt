package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Debt
import com.example.data.model.Product
import com.example.data.model.Sale
import com.example.ui.theme.FintechCharcoal
import com.example.ui.theme.FintechEmerald
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.VendoraViewModel

@Composable
fun GlobalSearchScreen(viewModel: VendoraViewModel) {
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val sales by viewModel.sales.collectAsStateWithLifecycle()
    val debts by viewModel.debts.collectAsStateWithLifecycle()

    val matchedProducts = remember(products, query) {
        if (query.isBlank()) emptyList<Product>()
        else products.filter {
            it.name.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true)
        }
    }

    val matchedSales = remember(sales, query) {
        if (query.isBlank()) emptyList<Sale>()
        else sales.filter {
            it.customerName.contains(query, ignoreCase = true) ||
            it.id.contains(query, ignoreCase = true) ||
            it.paymentMethod.contains(query, ignoreCase = true)
        }
    }

    val matchedDebts = remember(debts, query) {
        if (query.isBlank()) emptyList<Debt>()
        else debts.filter {
            it.debtor.contains(query, ignoreCase = true) ||
            it.phone.contains(query, ignoreCase = true) ||
            it.product.contains(query, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search products, receipts, debtors...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (query.isBlank()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🔍", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Universal Search", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text("Search across products, receipts, and debtor ledgers instantly.", color = TextSecondary, fontSize = 12.sp)
                }
            }
        } else {
            // Products section
            if (matchedProducts.isNotEmpty()) {
                item {
                    Text(
                        text = "Products (${matchedProducts.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = FintechEmerald
                    )
                }
                items(matchedProducts, key = { it.id }) { prod: Product ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(Icons.Default.Inventory2, contentDescription = null, tint = FintechEmerald)
                                Column {
                                    Text(prod.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                                    Text("Stock: ${prod.qty} • Category: ${prod.category}", fontSize = 11.sp, color = TextSecondary)
                                }
                            }
                            Text(viewModel.formatCurrency(prod.sellPrice), fontWeight = FontWeight.Black, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            // Sales section
            if (matchedSales.isNotEmpty()) {
                item {
                    Text(
                        text = "Sales Receipts (${matchedSales.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = FintechCharcoal
                    )
                }
                items(matchedSales, key = { it.id }) { sale: Sale ->
                    RecentSaleCard(sale = sale, viewModel = viewModel)
                }
            }

            // Debts section
            if (matchedDebts.isNotEmpty()) {
                item {
                    Text(
                        text = "Debts (${matchedDebts.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = WarningAmber
                    )
                }
                items(matchedDebts, key = { it.id }) { debt: Debt ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(Icons.Default.MoneyOff, contentDescription = null, tint = WarningAmber)
                                Column {
                                    Text(debt.debtor, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                                    Text("Due: ${debt.dueDate.split("T").firstOrNull() ?: debt.dueDate} • Phone: ${debt.phone.ifBlank { "N/A" }}", fontSize = 11.sp, color = TextSecondary)
                                }
                            }
                            Text(viewModel.formatCurrency(debt.amount), fontWeight = FontWeight.Black, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            if (matchedProducts.isEmpty() && matchedSales.isEmpty() && matchedDebts.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🔎", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "No records matched",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "No items or receipts matched '$query'.",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
