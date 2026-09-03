package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Sale
import com.example.ui.navigation.VendoraScreen
import com.example.ui.theme.DangerRed
import com.example.ui.theme.FintechEmerald
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.VendoraViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(viewModel: VendoraViewModel) {
    val context = LocalContext.current
    val products by viewModel.products.collectAsStateWithLifecycle()
    val sales by viewModel.sales.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()

    var chartDaysRange by remember { mutableStateOf(7) }

    // Date calculations
    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val displayDate = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date()) }
    val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
    val yesterdayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time) }

    val todaysSalesList = sales.filter { it.date.startsWith(todayStr) }
    val todaysSalesTotal = todaysSalesList.sumOf { it.total }
    val yesterdaysSalesTotal = sales.filter { it.date.startsWith(yesterdayStr) }.sumOf { it.total }

    val todaysProfit = todaysSalesList.sumOf { it.profit }
    val lowStockItems = products.filter { it.qty < 5 }

    // Clean percentage insight
    val salesInsight = if (yesterdaysSalesTotal > 0) {
        val diff = ((todaysSalesTotal - yesterdaysSalesTotal) / yesterdaysSalesTotal) * 100
        val sign = if (diff >= 0) "+" else ""
        "$sign${String.format(Locale.getDefault(), "%.0f", diff)}% vs yesterday"
    } else {
        "Today's turnover"
    }

    if (products.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(FintechEmerald.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Storefront,
                            contentDescription = null,
                            tint = FintechEmerald,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Welcome to ${appSettings?.businessName ?: "IBR SHOP"}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your store is initialized and ready. Register your first inventory item to start managing sales.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.navigateTo(VendoraScreen.AddProducts) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FintechEmerald,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier.testTag("add_first_product_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add First Product", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header with subtle date chip
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Overview",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Real-time activity for ${userProfile?.username ?: "Manager"}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Date",
                            modifier = Modifier.size(14.dp),
                            tint = TextSecondary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = displayDate,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Pulse KPI Grid (2x2 Balanced Grid on Mobile)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KpiMetricCard(
                        title = "Today's Sales",
                        value = viewModel.formatCurrency(todaysSalesTotal),
                        subtitle = salesInsight,
                        icon = Icons.Default.PointOfSale,
                        valueColor = MaterialTheme.colorScheme.onSurface,
                        subtitleColor = if (todaysSalesTotal >= yesterdaysSalesTotal) SuccessGreen else TextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    KpiMetricCard(
                        title = "Today's Profit",
                        value = viewModel.formatCurrency(todaysProfit),
                        subtitle = "Net margin",
                        icon = Icons.Default.AttachMoney,
                        valueColor = FintechEmerald,
                        subtitleColor = FintechEmerald,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KpiMetricCard(
                        title = "Low Stock Alerts",
                        value = "${lowStockItems.size}",
                        subtitle = if (lowStockItems.isNotEmpty()) "Action required" else "Stock healthy",
                        icon = Icons.Default.Inventory,
                        valueColor = if (lowStockItems.isNotEmpty()) DangerRed else MaterialTheme.colorScheme.onSurface,
                        subtitleColor = if (lowStockItems.isNotEmpty()) DangerRed else TextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    KpiMetricCard(
                        title = "Total Orders",
                        value = "${sales.size}",
                        subtitle = "Transactions logged",
                        icon = Icons.Default.Receipt,
                        valueColor = MaterialTheme.colorScheme.onSurface,
                        subtitleColor = TextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Executive Shortcuts
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Quick Actions",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.navigateTo(VendoraScreen.Sell) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FintechEmerald,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("quick_sale_button")
                        ) {
                            Icon(Icons.Default.PointOfSale, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("New Sale", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.navigateTo(VendoraScreen.AddProducts) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("add_product_shortcut")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Item", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.navigateTo(VendoraScreen.Purchases) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Purchases", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        OutlinedButton(
                            onClick = {
                                val csv = buildString {
                                    appendLine("Date,Customer,Method,Total,Profit")
                                    sales.forEach {
                                        appendLine("${it.date},${it.customerName},${it.paymentMethod},${it.total},${it.profit}")
                                    }
                                }
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, csv)
                                    type = "text/csv"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Export Sales CSV"))
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export CSV", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    // Low Stock Alert inside card if any
                    if (lowStockItems.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "RESTOCK NEEDED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarningAmber,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        lowStockItems.take(3).forEach { prod ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = prod.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Surface(
                                    color = if (prod.qty == 0) DangerRed else WarningAmber,
                                    shape = RoundedCornerShape(50.dp)
                                ) {
                                    Text(
                                        text = if (prod.qty == 0) "Out of Stock" else "${prod.qty} left",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Sales Performance Trend Chart
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sales Performance",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                shape = RoundedCornerShape(50.dp),
                                color = if (chartDaysRange == 7) FintechEmerald else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { chartDaysRange = 7 }
                            ) {
                                Text(
                                    "7D",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (chartDaysRange == 7) Color.White else TextSecondary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(50.dp),
                                color = if (chartDaysRange == 30) FintechEmerald else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { chartDaysRange = 30 }
                            ) {
                                Text(
                                    "30D",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (chartDaysRange == 30) Color.White else TextSecondary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    SalesTrendCanvas(sales = sales, daysRange = chartDaysRange)
                }
            }
        }

        // Recent Activity Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${sales.size} total",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        if (sales.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No sales recorded yet.",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        } else {
            items(sales.takeLast(10).reversed(), key = { it.id }) { sale ->
                RecentSaleCard(sale = sale, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun KpiMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    valueColor: Color,
    subtitleColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                color = valueColor
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = subtitleColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun RecentSaleCard(sale: Sale, viewModel: VendoraViewModel) {
    val items = viewModel.repository.parseCartItems(sale.itemsJson)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.showReceiptForSale(sale) }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (sale.paymentMethod == "Debt") Icons.Default.Warning else Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = if (sale.paymentMethod == "Debt") WarningAmber else FintechEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = sale.customerName.ifBlank { "Walk-in Customer" },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${items.size} item(s) • ${sale.paymentMethod}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = viewModel.formatCurrency(sale.total),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = sale.date.split("T").firstOrNull() ?: "",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun SalesTrendCanvas(sales: List<Sale>, daysRange: Int) {
    val days = remember(daysRange) {
        (daysRange - 1 downTo 0).map { offset ->
            val c = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -offset) }
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(c.time)
        }
    }

    val dailyTotals = remember(sales, days) {
        days.map { dayStr ->
            sales.filter { it.date.startsWith(dayStr) }.sumOf { it.total }.toFloat()
        }
    }

    val maxVal = (dailyTotals.maxOrNull() ?: 100f).coerceAtLeast(100f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height - 20.dp.toPx()
            val stepX = if (dailyTotals.size > 1) width / (dailyTotals.size - 1) else width

            // Grid lines
            for (i in 1..3) {
                val y = height * (i / 4f)
                drawLine(
                    color = Color(0xFFE5E7EB).copy(alpha = 0.6f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            val path = Path()
            val fillPath = Path()

            dailyTotals.forEachIndexed { index, total ->
                val x = index * stepX
                val normalizedY = height - (total / maxVal) * height
                if (index == 0) {
                    path.moveTo(x, normalizedY)
                    fillPath.moveTo(x, height)
                    fillPath.lineTo(x, normalizedY)
                } else {
                    path.lineTo(x, normalizedY)
                    fillPath.lineTo(x, normalizedY)
                }
            }

            fillPath.lineTo(width, height)
            fillPath.close()

            // Fill gradient
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(FintechEmerald.copy(alpha = 0.20f), Color.Transparent),
                    startY = 0f,
                    endY = height
                )
            )

            // Line stroke
            drawPath(
                path = path,
                color = FintechEmerald,
                style = Stroke(width = 2.5.dp.toPx())
            )

            // Minimalist dots
            dailyTotals.forEachIndexed { index, total ->
                val x = index * stepX
                val y = height - (total / maxVal) * height
                drawCircle(
                    color = Color.White,
                    radius = 3.dp.toPx(),
                    center = Offset(x, y)
                )
                drawCircle(
                    color = FintechEmerald,
                    radius = 1.5.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }
    }
}
