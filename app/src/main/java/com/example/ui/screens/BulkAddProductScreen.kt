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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Product
import com.example.ui.navigation.VendoraScreen
import com.example.ui.theme.DangerRed
import com.example.ui.theme.FintechEmerald
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.VendoraViewModel

data class ProductDraft(
    var name: String = "",
    var category: String = "General",
    var qty: String = "10",
    var buyPrice: String = "0",
    var sellPrice: String = "0"
)

@Composable
fun BulkAddProductScreen(viewModel: VendoraViewModel) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val drafts = remember {
        mutableStateListOf(
            ProductDraft(),
            ProductDraft()
        )
    }

    var showPasswordDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Bulk Product Entry",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Add multiple inventory items at once or load sample data.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        drafts.add(ProductDraft("Bluetooth Speaker", "Electronics", "15", "8500", "13000"))
                        drafts.add(ProductDraft("Cotton Casual Shirt", "Fashion", "20", "4000", "7500"))
                        drafts.add(ProductDraft("Coffee Beans 500g", "Groceries", "30", "3000", "5000"))
                        viewModel.showToast("Loaded sample products!")
                    },
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Load Samples", fontSize = 12.sp)
                }

                Button(
                    onClick = { drafts.add(ProductDraft()) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FintechEmerald,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier.weight(1f).testTag("add_row_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Row", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        itemsIndexed(drafts) { index, draft ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Item #${index + 1}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = FintechEmerald
                        )
                        if (drafts.size > 1) {
                            IconButton(
                                onClick = { drafts.removeAt(index) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = DangerRed.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = draft.name,
                        onValueChange = { draft.name = it },
                        label = { Text("Product Name *") },
                        modifier = Modifier.fillMaxWidth().testTag("product_name_input_$index")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = draft.category,
                            onValueChange = { draft.category = it },
                            label = { Text("Category") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = draft.qty,
                            onValueChange = { draft.qty = it },
                            label = { Text("Stock Qty") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = draft.buyPrice,
                            onValueChange = { draft.buyPrice = it },
                            label = { Text("Buy Price (₦)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = draft.sellPrice,
                            onValueChange = { draft.sellPrice = it },
                            label = { Text("Sell Price (₦)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.navigateTo(VendoraScreen.Inventory) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        val validDrafts = drafts.filter { it.name.isNotBlank() }
                        if (validDrafts.isEmpty()) {
                            viewModel.showToast("Please enter at least one valid product name.")
                            return@Button
                        }
                        showPasswordDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FintechEmerald,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1.5f).height(48.dp).testTag("confirm_bulk_save_btn")
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Products", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showPasswordDialog) {
        var passwordInput by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            icon = { Icon(Icons.Default.Lock, contentDescription = null, tint = FintechEmerald) },
            title = { Text("Security Verification", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter admin password to finalize adding products.", fontSize = 13.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it; errorMessage = null },
                        label = { Text("Admin Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = errorMessage != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (errorMessage != null) {
                        Text(errorMessage!!, color = DangerRed, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val correctPass = userProfile?.password ?: "1234"
                        if (passwordInput == correctPass) {
                            val validProducts = drafts
                                .filter { it.name.isNotBlank() }
                                .map { d ->
                                    Product(
                                        name = d.name.trim(),
                                        category = d.category.trim().ifBlank { "General" },
                                        qty = d.qty.toIntOrNull() ?: 0,
                                        buyPrice = d.buyPrice.toDoubleOrNull() ?: 0.0,
                                        sellPrice = d.sellPrice.toDoubleOrNull() ?: 0.0
                                    )
                                }
                            viewModel.bulkAddProducts(validProducts) {
                                showPasswordDialog = false
                                viewModel.navigateTo(VendoraScreen.Inventory)
                            }
                        } else {
                            errorMessage = "Incorrect admin password."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FintechEmerald)
                ) {
                    Text("Confirm", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
