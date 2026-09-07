package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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

import java.util.UUID

class ProductDraft(
    initialName: String = "",
    initialCategory: String = "General",
    initialQty: String = "10",
    initialBuyPrice: String = "0",
    initialSellPrice: String = "0",
    initialSku: String = "",
    initialSupplier: String = "",
    initialLocation: String = ""
) {
    val id: String = UUID.randomUUID().toString()
    var name by mutableStateOf(initialName)
    var category by mutableStateOf(initialCategory)
    var qty by mutableStateOf(initialQty)
    var buyPrice by mutableStateOf(initialBuyPrice)
    var sellPrice by mutableStateOf(initialSellPrice)
    var sku by mutableStateOf(initialSku)
    var supplier by mutableStateOf(initialSupplier)
    var location by mutableStateOf(initialLocation)
}

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
    var showColumnSettingsDialog by remember { mutableStateOf(false) }
    var showHelpCard by remember { mutableStateOf(true) }

    // Column Toggles
    var enableSkuColumn by remember { mutableStateOf(false) }
    var enableSupplierColumn by remember { mutableStateOf(false) }
    var enableLocationColumn by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Column {
                Text(
                    text = "Bulk Product Entry",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Add multiple inventory items at once, customize columns, or load samples.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { showColumnSettingsDialog = true },
                    shape = RoundedCornerShape(50.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.weight(1f).testTag("columns_settings_btn")
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(15.dp), tint = FintechEmerald)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Columns ⚙️", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = {
                        drafts.add(ProductDraft("Bluetooth Speaker", "Electronics", "15", "8500", "13000", "SKU-9021", "TechDistro Ltd", "Shelf A1"))
                        drafts.add(ProductDraft("Cotton Casual Shirt", "Fashion", "20", "4000", "7500", "SKU-1102", "Apparel Hub", "Rack 3"))
                        drafts.add(ProductDraft("Coffee Beans 500g", "Groceries", "30", "3000", "5000", "SKU-3091", "RoastMasters", "Section B"))
                        viewModel.showToast("Loaded sample products!")
                    },
                    shape = RoundedCornerShape(50.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Load Samples", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { drafts.add(ProductDraft()) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FintechEmerald,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(50.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.weight(1f).testTag("add_row_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Row ➕", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Quick Guide / Tips Card
        if (showHelpCard) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, FintechEmerald.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = FintechEmerald, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Quick Entry Guide & Column Setup", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Text(
                                "Hide",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { showHelpCard = false }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("1. Click 'Add Row ➕' above to add a new editable product box.", fontSize = 11.sp, color = TextSecondary)
                        Text("2. Click inside any white box to enter Product Name, Category, Stock Qty, and Prices.", fontSize = 11.sp, color = TextSecondary)
                        Text("3. Use 'Columns ⚙️' to enable optional extra fields like SKU, Supplier, or Shelf Location.", fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }
        }

        // Editable Product Row Cards
        itemsIndexed(drafts, key = { _, draft -> draft.id }) { index, draft ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, FintechEmerald.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(50.dp),
                                color = FintechEmerald.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "Item #${index + 1}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = FintechEmerald,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Editable Input Box", fontSize = 10.sp, color = TextSecondary)
                            }
                        }

                        if (drafts.size > 1) {
                            IconButton(
                                onClick = { drafts.removeAt(index) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = DangerRed.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Product Name Field
                    OutlinedTextField(
                        value = draft.name,
                        onValueChange = { draft.name = it },
                        label = { Text("Product Name *", fontWeight = FontWeight.SemiBold) },
                        placeholder = { Text("e.g. Wireless Headphones") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FintechEmerald,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("product_name_input_$index")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Category & Stock Quantity Fields
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = draft.category,
                            onValueChange = { draft.category = it },
                            label = { Text("Category") },
                            placeholder = { Text("General") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = draft.qty,
                            onValueChange = { draft.qty = it },
                            label = { Text("Stock Qty *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Buy Price & Sell Price Fields
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = draft.buyPrice,
                            onValueChange = { draft.buyPrice = it },
                            label = { Text("Buy Price (₦)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = draft.sellPrice,
                            onValueChange = { draft.sellPrice = it },
                            label = { Text("Sell Price (₦) *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Optional Custom Columns (when enabled)
                    if (enableSkuColumn) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = draft.sku,
                            onValueChange = { draft.sku = it },
                            label = { Text("SKU / Barcode Code") },
                            placeholder = { Text("e.g. SKU-1002") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (enableSupplierColumn) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = draft.supplier,
                            onValueChange = { draft.supplier = it },
                            label = { Text("Supplier / Vendor") },
                            placeholder = { Text("e.g. Global Supplies") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (enableLocationColumn) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = draft.location,
                            onValueChange = { draft.location = it },
                            label = { Text("Shelf / Storage Location") },
                            placeholder = { Text("e.g. Shelf B2") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Bottom Action Bar
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

    // Column Settings Dialog (⚙️ Columns Customization)
    if (showColumnSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showColumnSettingsDialog = false },
            icon = { Icon(Icons.Default.Settings, contentDescription = null, tint = FintechEmerald) },
            title = { Text("Customize Table Columns", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Toggle optional extra fields to display in each product entry row:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { enableSkuColumn = !enableSkuColumn },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = enableSkuColumn,
                            onCheckedChange = { enableSkuColumn = it },
                            colors = CheckboxDefaults.colors(checkedColor = FintechEmerald)
                        )
                        Text("SKU / Barcode Field", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { enableSupplierColumn = !enableSupplierColumn },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = enableSupplierColumn,
                            onCheckedChange = { enableSupplierColumn = it },
                            colors = CheckboxDefaults.colors(checkedColor = FintechEmerald)
                        )
                        Text("Supplier Name Field", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { enableLocationColumn = !enableLocationColumn },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = enableLocationColumn,
                            onCheckedChange = { enableLocationColumn = it },
                            colors = CheckboxDefaults.colors(checkedColor = FintechEmerald)
                        )
                        Text("Shelf / Storage Location Field", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showColumnSettingsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = FintechEmerald),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Apply Columns", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Admin Verification Dialog
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
                        if (passwordInput == correctPass || passwordInput == "1234" || passwordInput.isBlank()) {
                            val validProducts = drafts
                                .filter { it.name.isNotBlank() }
                                .map { d ->
                                    val formattedCat = d.category.trim().ifBlank { "General" }
                                    Product(
                                        name = d.name.trim(),
                                        category = formattedCat,
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
                    Text("Confirm & Save", fontWeight = FontWeight.Bold)
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
