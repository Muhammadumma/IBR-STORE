package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppSettings
import com.example.data.model.CartItem
import com.example.data.model.Sale
import com.example.ui.theme.FintechEmerald
import com.example.ui.viewmodel.VendoraViewModel

@Composable
fun ReceiptDialog(
    sale: Sale,
    settings: AppSettings?,
    viewModel: VendoraViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val items: List<CartItem> = viewModel.repository.parseCartItems(sale.itemsJson)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TRANSACTION RECEIPT",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280),
                        letterSpacing = 1.sp
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.DarkGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Business Logo & Info
                Text(
                    text = settings?.businessName ?: "IBR SHOP",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                if (!settings?.businessAddress.isNullOrBlank()) {
                    Text(
                        text = settings.businessAddress,
                        fontSize = 12.sp,
                        color = Color(0xFF4B5563),
                        textAlign = TextAlign.Center
                    )
                }
                Text(
                    text = "Tel: ${settings?.businessPhone ?: "+234 800 000 0000"}",
                    fontSize = 12.sp,
                    color = Color(0xFF4B5563),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Receipt Meta Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF9FAFB), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Receipt #:", fontSize = 12.sp, color = Color(0xFF6B7280))
                            Text(
                                "#${sale.id.takeLast(8)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = Color.Black
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Date:", fontSize = 12.sp, color = Color(0xFF6B7280))
                            Text(sale.date.replace("T", " "), fontSize = 12.sp, color = Color.Black)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Payment Method:", fontSize = 12.sp, color = Color(0xFF6B7280))
                            Text(
                                sale.paymentMethod,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = FintechEmerald
                            )
                        }
                        if (sale.customerName.isNotBlank() && sale.customerName != "Walk-in Customer") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Customer:", fontSize = 12.sp, color = Color(0xFF6B7280))
                                Text(sale.customerName, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Items Table
                HorizontalDivider(color = Color.LightGray)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Item", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.weight(1.8f))
                    Text("Qty", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black, textAlign = TextAlign.Center, modifier = Modifier.weight(0.6f))
                    Text("Total", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black, textAlign = TextAlign.End, modifier = Modifier.weight(1.2f))
                }
                HorizontalDivider(color = Color.LightGray)

                items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.name,
                            fontSize = 13.sp,
                            color = Color.Black,
                            modifier = Modifier.weight(1.8f)
                        )
                        Text(
                            text = "${item.qty}",
                            fontSize = 13.sp,
                            color = Color.DarkGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(0.6f)
                        )
                        Text(
                            text = viewModel.formatCurrency(item.price * item.qty),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1.2f)
                        )
                    }
                }

                HorizontalDivider(color = Color.LightGray, modifier = Modifier.padding(top = 8.dp))

                // Totals
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal", fontSize = 13.sp, color = Color(0xFF4B5563))
                        Text(viewModel.formatCurrency(sale.subtotal), fontSize = 13.sp, color = Color.Black)
                    }
                    if (sale.vat > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("VAT (${settings?.vatRate ?: 15.0}%)", fontSize = 13.sp, color = Color(0xFF4B5563))
                            Text(viewModel.formatCurrency(sale.vat), fontSize = 13.sp, color = Color.Black)
                        }
                    }
                    HorizontalDivider(color = Color.Black, modifier = Modifier.padding(vertical = 4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("TOTAL AMOUNT", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        Text(
                            viewModel.formatCurrency(sale.total),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = FintechEmerald
                        )
                    }
                }

                // Footer Note
                Text(
                    text = settings?.receiptFooter ?: "Thank you for your patronage!",
                    fontSize = 11.sp,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val shareText = buildString {
                                appendLine("=== ${settings?.businessName ?: "IBR SHOP"} ===")
                                appendLine("Receipt #${sale.id.takeLast(8)}")
                                appendLine("Date: ${sale.date}")
                                appendLine("Payment: ${sale.paymentMethod}")
                                appendLine("------------------------")
                                items.forEach {
                                    appendLine("${it.name} x${it.qty} = ${viewModel.formatCurrency(it.price * it.qty)}")
                                }
                                appendLine("------------------------")
                                appendLine("Total: ${viewModel.formatCurrency(sale.total)}")
                                appendLine(settings?.receiptFooter ?: "Thank you for your patronage!")
                            }
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Receipt"))
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share", fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            viewModel.showToast("Receipt print initiated 🖨️")
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = FintechEmerald),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Print", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
