package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Product
import com.example.data.model.Sale
import com.example.ui.theme.DangerRed
import com.example.ui.theme.FintechEmerald
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.VendoraViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAnalyticsScreen(viewModel: VendoraViewModel) {
    val sales by viewModel.sales.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val debts by viewModel.debts.collectAsStateWithLifecycle()
    val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()
    val aiResponse by viewModel.aiResponse.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()

    var aiQueryInput by remember { mutableStateOf("") }
    var selectedProductId by remember { mutableStateOf("all") }
    var productDropdownExpanded by remember { mutableStateOf(false) }
    var showAiConfigDialog by remember { mutableStateOf(false) }

    val totalRevenue = remember(sales) { sales.sumOf { it.total } }
    val totalProfit = remember(sales) { sales.sumOf { it.profit } }

    val selectedProduct = remember(products, selectedProductId) {
        if (selectedProductId == "all") null else products.find { it.id == selectedProductId }
    }

    // Day of week stats (Sunday to Saturday)
    val dayNames = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    val shortDayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    val dayStats = remember(sales, selectedProductId) {
        val stats = (0..6).map { dayIdx ->
            var units = 0
            var txns = 0
            sales.forEach { sale ->
                try {
                    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(sale.date.split("T").firstOrNull() ?: sale.date)
                    if (date != null) {
                        val c = Calendar.getInstance().apply { time = date }
                        if (c.get(Calendar.DAY_OF_WEEK) - 1 == dayIdx) {
                            val items = viewModel.repository.parseCartItems(sale.itemsJson)
                            if (selectedProductId == "all") {
                                txns += 1
                                units += items.sumOf { it.qty }
                            } else {
                                val match = items.find { it.productId == selectedProductId }
                                if (match != null) {
                                    txns += 1
                                    units += match.qty
                                }
                            }
                        }
                    }
                } catch (_: Exception) {}
            }
            Triple(shortDayNames[dayIdx], units, txns)
        }
        stats
    }

    val peakDayIndex = remember(dayStats) {
        var maxUnits = -1
        var bestIdx = 0
        dayStats.forEachIndexed { index, triple ->
            if (triple.second > maxUnits) {
                maxUnits = triple.second
                bestIdx = index
            }
        }
        bestIdx
    }

    val peakDayName = dayNames[peakDayIndex]
    val peakDayUnits = dayStats[peakDayIndex].second
    val totalUnitsSold = remember(dayStats) { dayStats.sumOf { it.second } }
    val avgDailyUnits = totalUnitsSold / 7f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Page Title & Key Status
        item {
            val hasCustomKey = !appSettings?.customGeminiApiKey.isNullOrBlank()
            val activeModel = appSettings?.geminiModel ?: "gemini-3.5-flash"

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = "AI Analytics & Demand",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Powered by Google Gemini ($activeModel)",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = if (hasCustomKey) FintechEmerald.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable { showAiConfigDialog = !showAiConfigDialog }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "API Key",
                            tint = if (hasCustomKey) FintechEmerald else TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (hasCustomKey) "Custom Key" else "Configure",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (hasCustomKey) FintechEmerald else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Expandable Custom Gemini API Settings
        item {
            AnimatedVisibility(visible = showAiConfigDialog) {
                var tempKey by remember(appSettings) { mutableStateOf(appSettings?.customGeminiApiKey ?: "") }
                var tempModel by remember(appSettings) { mutableStateOf(appSettings?.geminiModel ?: "gemini-3.5-flash") }
                var isTesting by remember { mutableStateOf(false) }
                var testResultMsg by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Custom Gemini API Setup", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "Enter your personal Gemini API key from Google AI Studio for custom quotas.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        OutlinedTextField(
                            value = tempKey,
                            onValueChange = {
                                tempKey = it
                                testResultMsg = null
                            },
                            placeholder = { Text("Paste AIzaSy... API key", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val modelOptions = listOf(
                                "gemini-3.5-flash" to "3.5 Flash",
                                "gemini-3.1-pro-preview" to "3.1 Pro",
                                "gemini-2.5-flash" to "2.5 Flash"
                            )
                            modelOptions.forEach { (mId, mLabel) ->
                                val isSel = tempModel == mId
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) FintechEmerald else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.weight(1f).clickable { tempModel = mId }
                                ) {
                                    Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                        Text(
                                            mLabel,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        if (testResultMsg != null) {
                            val (success, text) = testResultMsg!!
                            Text(text, fontSize = 11.sp, color = if (success) SuccessGreen else DangerRed)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    isTesting = true
                                    testResultMsg = null
                                    viewModel.testGeminiApiKey(tempKey, tempModel) { ok, msg ->
                                        isTesting = false
                                        testResultMsg = Pair(ok, msg)
                                    }
                                },
                                enabled = !isTesting && tempKey.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                if (isTesting) {
                                    CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 2.dp)
                                } else {
                                    Text("Test Key", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            Button(
                                onClick = {
                                    viewModel.saveGeminiConfig(tempKey, tempModel)
                                    showAiConfigDialog = false
                                    viewModel.showToast("Gemini settings updated!")
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = FintechEmerald,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Save Key", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Financial Health Cards (2-column balanced grid)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Gross Revenue", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = viewModel.formatCurrency(totalRevenue),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${sales.size} sales logged", fontSize = 11.sp, color = TextMuted)
                    }
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Gross Margin", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = viewModel.formatCurrency(totalProfit),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = FintechEmerald
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val marginPct = if (totalRevenue > 0) (totalProfit / totalRevenue) * 100 else 0.0
                        Text("${String.format(Locale.getDefault(), "%.1f", marginPct)}% profit margin", fontSize = 11.sp, color = SuccessGreen)
                    }
                }
            }
        }

        // AI Query & Advisor Card
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
                        text = "AI Business Advisor",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick Prompt Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val quickPrompts = listOf(
                            "Margins" to "Analyze profit margins and highlight high-performing vs low-margin products.",
                            "Restock" to "Which products need immediate restocking based on inventory quantities?",
                            "Debt Risk" to "Evaluate our current active debt vs total inventory value and recommend recovery actions."
                        )
                        quickPrompts.forEach { (label, promptText) ->
                            Surface(
                                shape = RoundedCornerShape(50.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        aiQueryInput = promptText
                                        viewModel.askGemini(promptText)
                                    }
                            ) {
                                Box(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = aiQueryInput,
                            onValueChange = { aiQueryInput = it },
                            placeholder = { Text("Ask about sales, stock, margins...", fontSize = 12.sp, color = TextMuted) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("ai_query_input")
                        )

                        Button(
                            onClick = {
                                if (aiQueryInput.isNotBlank()) {
                                    viewModel.askGemini(aiQueryInput.trim())
                                }
                            },
                            enabled = !isAiLoading && aiQueryInput.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FintechEmerald,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("ask_ai_btn")
                        ) {
                            if (isAiLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ask", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // AI Result Display
                    AnimatedVisibility(visible = aiResponse != null || isAiLoading) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Lightbulb,
                                            contentDescription = null,
                                            tint = FintechEmerald,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            "Gemini Insight",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = FintechEmerald
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = aiResponse ?: "Analyzing store transactions...",
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Product Velocity & Demand Prediction Card
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
                        text = "Day-of-Week Velocity",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Sales volume distribution across the week",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Product Selector
                    ExposedDropdownMenuBox(
                        expanded = productDropdownExpanded,
                        onExpandedChange = { productDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = if (selectedProductId == "all") "All Products (Store Aggregate)" else (selectedProduct?.name ?: "All"),
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = productDropdownExpanded) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = productDropdownExpanded,
                            onDismissRequest = { productDropdownExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All Products (Store Aggregate)", fontWeight = FontWeight.Bold) },
                                onClick = {
                                    selectedProductId = "all"
                                    productDropdownExpanded = false
                                }
                            )
                            products.forEach { prod ->
                                DropdownMenuItem(
                                    text = { Text(prod.name) },
                                    onClick = {
                                        selectedProductId = prod.id
                                        productDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Day of week Bar Chart
                    WeeklyVelocityCanvas(dayStats = dayStats)

                    Spacer(modifier = Modifier.height(16.dp))

                    // High Demand Alert Pill Card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "PEAK DEMAND: ${peakDayName.uppercase()}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FintechEmerald
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (peakDayUnits > 0)
                                    "Historical data indicates ${peakDayName}s generate your peak sales ($peakDayUnits units), compared to a daily average of ${String.format(Locale.getDefault(), "%.1f", avgDailyUnits)} units."
                                else
                                    "Log more transactions to enable day-of-week velocity projections.",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeeklyVelocityCanvas(dayStats: List<Triple<String, Int, Int>>) {
    val maxUnits = (dayStats.maxOfOrNull { it.second } ?: 10).coerceAtLeast(5).toFloat()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height - 24.dp.toPx()
            val colWidth = width / dayStats.size

            dayStats.forEachIndexed { index, triple ->
                val x = index * colWidth + (colWidth * 0.25f)
                val barW = colWidth * 0.5f
                val barH = (triple.second / maxUnits) * height
                val y = height - barH

                // Minimalist column bar
                drawRoundRect(
                    color = FintechEmerald.copy(alpha = if (triple.second > 0) 0.85f else 0.15f),
                    topLeft = Offset(x, y),
                    size = Size(barW, barH.coerceAtLeast(4.dp.toPx())),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                )
            }
        }

        // Clean text labels at bottom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            dayStats.forEach { triple ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(triple.first, fontSize = 10.sp, color = TextSecondary)
                    Text(
                        "${triple.second}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
