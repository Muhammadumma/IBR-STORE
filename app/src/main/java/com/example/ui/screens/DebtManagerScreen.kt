package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Debt
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
fun DebtManagerScreen(viewModel: VendoraViewModel) {
    val context = LocalContext.current
    val debts by viewModel.debts.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()

    var showAddDebtDialog by remember { mutableStateOf(false) }
    var selectedDebtForReason by remember { mutableStateOf<Debt?>(null) }
    var debtToSettle by remember { mutableStateOf<Debt?>(null) }

    val totalStockValue = remember(products) { products.sumOf { it.qty * it.buyPrice } }
    val activeDebtTotal = remember(debts) { debts.filter { it.status != "Paid" }.sumOf { it.amount } }
    val debtCap = totalStockValue * 0.30

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = "Debt Management",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Track debtor balances and 30% risk exposure cap.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = { showAddDebtDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FintechEmerald,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(50.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                    modifier = Modifier.testTag("record_debt_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Debt", fontSize = 12.sp, fontWeight = FontWeight.Bold, softWrap = false)
                }
            }
        }

        // Debt Cap Indicator Card
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
                        Text("Active Receivables", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = viewModel.formatCurrency(activeDebtTotal),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = if (activeDebtTotal > debtCap) DangerRed else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            if (activeDebtTotal > debtCap) "Cap exceeded!" else "Within risk limit",
                            fontSize = 11.sp,
                            color = if (activeDebtTotal > debtCap) DangerRed else SuccessGreen,
                            fontWeight = FontWeight.SemiBold
                        )
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
                        Text("Stock Debt Cap (30%)", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = viewModel.formatCurrency(debtCap),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Max credit allowance", fontSize = 11.sp, color = TextMuted)
                    }
                }
            }
        }

        if (debts.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🤝", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "No active debt records",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "All customer accounts are settled in full.",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            items(debts, key = { it.id }) { debt ->
                DebtItemCard(
                    debt = debt,
                    viewModel = viewModel,
                    onSettle = { debtToSettle = debt },
                    onCall = {
                        if (debt.phone.isNotBlank()) {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${debt.phone}"))
                            context.startActivity(intent)
                        } else {
                            viewModel.showToast("No phone number on record.")
                        }
                    },
                    onAddReason = { selectedDebtForReason = debt }
                )
            }
        }
    }

    // Add Debt Dialog
    if (showAddDebtDialog) {
        RecordDebtDialog(
            onDismiss = { showAddDebtDialog = false },
            onConfirm = { debtor, phone, product, amount, dueDate ->
                viewModel.addDebtManual(debtor, phone, product, amount, dueDate) {
                    showAddDebtDialog = false
                }
            }
        )
    }

    // Settle Debt Confirmation
    if (debtToSettle != null) {
        AlertDialog(
            onDismissRequest = { debtToSettle = null },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FintechEmerald) },
            title = { Text("Settle Debt Record", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Confirm full settlement of ${viewModel.formatCurrency(debtToSettle!!.amount)} from '${debtToSettle!!.debtor}'? This will mark the debt as Paid and log a cash inflow.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.settleDebt(debtToSettle!!.id)
                        debtToSettle = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FintechEmerald),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Confirm Settlement", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { debtToSettle = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Overdue Reason Dialog
    if (selectedDebtForReason != null) {
        var reasonInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { selectedDebtForReason = null },
            icon = { Icon(Icons.Default.Comment, contentDescription = null, tint = FintechEmerald) },
            title = { Text("Log Follow-Up Note", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Debtor: ${selectedDebtForReason?.debtor}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reasonInput,
                        onValueChange = { reasonInput = it },
                        label = { Text("Reason or repayment commitment") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (reasonInput.isNotBlank()) {
                            viewModel.addOverdueReason(selectedDebtForReason!!.id, reasonInput.trim())
                            selectedDebtForReason = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FintechEmerald),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Save Note", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedDebtForReason = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DebtItemCard(
    debt: Debt,
    viewModel: VendoraViewModel,
    onSettle: () -> Unit,
    onCall: () -> Unit,
    onAddReason: () -> Unit
) {
    val isPaid = debt.status == "Paid"
    val isOverdue = remember(debt.dueDate, isPaid) {
        if (isPaid) false
        else {
            try {
                val due = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(debt.dueDate.split("T").firstOrNull() ?: debt.dueDate)
                due != null && due.before(Date())
            } catch (_: Exception) {
                false
            }
        }
    }

    val reasons = remember(debt.overdueReasonsJson) {
        viewModel.repository.parseOverdueReasons(debt.overdueReasonsJson)
    }

    val statusColor = when {
        isPaid -> SuccessGreen
        isOverdue -> DangerRed
        else -> WarningAmber
    }

    val statusText = when {
        isPaid -> "Paid"
        isOverdue -> "Overdue"
        else -> "Pending"
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, if (isOverdue) DangerRed.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = debt.debtor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${debt.phone.ifBlank { "No phone" }} • Item: ${debt.product}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = statusColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = statusText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Due Date", fontSize = 11.sp, color = TextSecondary)
                    Text(
                        text = debt.dueDate.split("T").firstOrNull() ?: debt.dueDate,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isOverdue) DangerRed else MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = viewModel.formatCurrency(debt.amount),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Reason logs if any
            if (reasons.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    reasons.takeLast(2).forEach { r ->
                        Text("• $r", fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }

            if (!isPaid) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onSettle,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FintechEmerald,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Settle Debt", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onCall,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        modifier = Modifier.weight(0.7f)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Call", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = onAddReason,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        modifier = Modifier.weight(0.7f)
                    ) {
                        Icon(Icons.Default.Comment, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Note", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun RecordDebtDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Double, String) -> Unit
) {
    var debtor by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var product by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var dueDateStr by remember {
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 7) }
        mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time))
    }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record New Debt", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = debtor,
                    onValueChange = { debtor = it; errorMsg = null },
                    label = { Text("Debtor Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = product,
                    onValueChange = { product = it },
                    label = { Text("Product / Item Description") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it; errorMsg = null },
                    label = { Text("Debt Amount (₦)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dueDateStr,
                    onValueChange = { dueDateStr = it },
                    label = { Text("Due Date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (errorMsg != null) {
                    Text(errorMsg!!, color = DangerRed, fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountStr.toDoubleOrNull()
                    if (debtor.isBlank() || amt == null || amt <= 0) {
                        errorMsg = "Please enter debtor name and a valid amount."
                        return@Button
                    }
                    onConfirm(debtor.trim(), phone.trim(), product.trim(), amt, dueDateStr.trim())
                },
                colors = ButtonDefaults.buttonColors(containerColor = FintechEmerald),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Record Debt", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
