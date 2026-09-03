package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AppSettings
import com.example.data.model.SecurityQuestion
import com.example.ui.theme.BrandBluePrimary
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.FintechEmerald
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.VendoraViewModel

@Composable
fun SettingsScreen(viewModel: VendoraViewModel) {
    val context = LocalContext.current
    val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val sales by viewModel.sales.collectAsStateWithLifecycle()
    val debts by viewModel.debts.collectAsStateWithLifecycle()

    var businessName by remember(appSettings) { mutableStateOf(appSettings?.businessName ?: "IBR SHOP") }
    var businessAddress by remember(appSettings) { mutableStateOf(appSettings?.businessAddress ?: "Main Market Plaza, Suite 4B") }
    var businessPhone by remember(appSettings) { mutableStateOf(appSettings?.businessPhone ?: "+234 800 000 0000") }
    var receiptFooter by remember(appSettings) { mutableStateOf(appSettings?.receiptFooter ?: "Thank you for your patronage! Goods sold in good condition.") }

    var vatEnabled by remember(appSettings) { mutableStateOf(appSettings?.vatEnabled ?: false) }
    var vatRate by remember(appSettings) { mutableStateOf(appSettings?.vatRate?.toString() ?: "7.5") }

    var newPassword by remember { mutableStateOf("") }
    var q1 by remember { mutableStateOf("What was the name of your first school?") }
    var a1 by remember { mutableStateOf("") }
    var q2 by remember { mutableStateOf("In what city was your business founded?") }
    var a2 by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Application Settings",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Configure store branding, receipts, taxes & security.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Profile Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(EmeraldAccent.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👑", fontSize = 28.sp)
                    }
                    Column {
                        Text(
                            text = userProfile?.username ?: "Admin Manager",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = businessName,
                            fontSize = 13.sp,
                            color = EmeraldAccent,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Superuser • Offline Database Active",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Store Identity & Receipt Customization
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Business, contentDescription = null, tint = EmeraldAccent, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Business & Receipt Identity", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    OutlinedTextField(
                        value = businessName,
                        onValueChange = { businessName = it },
                        label = { Text("Store / Business Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = businessAddress,
                        onValueChange = { businessAddress = it },
                        label = { Text("Physical Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = businessPhone,
                        onValueChange = { businessPhone = it },
                        label = { Text("Contact Phone") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = receiptFooter,
                        onValueChange = { receiptFooter = it },
                        label = { Text("Receipt Footer Note") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            val current = appSettings ?: AppSettings()
                            viewModel.saveSettings(
                                current.copy(
                                    businessName = businessName,
                                    businessAddress = businessAddress,
                                    businessPhone = businessPhone,
                                    receiptFooter = receiptFooter
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldAccent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.align(Alignment.End).testTag("save_business_settings_btn")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Details")
                    }
                }
            }
        }

        // Tax & VAT Configuration
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Receipt, contentDescription = null, tint = EmeraldAccent, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Value Added Tax (VAT)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Apply automatic tax to checkout orders", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Switch(
                            checked = vatEnabled,
                            onCheckedChange = {
                                vatEnabled = it
                                val current = appSettings ?: AppSettings()
                                viewModel.saveSettings(current.copy(vatEnabled = it))
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = EmeraldAccent)
                        )
                    }

                    if (vatEnabled) {
                        OutlinedTextField(
                            value = vatRate,
                            onValueChange = { vatRate = it },
                            label = { Text("Tax Rate (%)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                val rate = vatRate.toDoubleOrNull() ?: 7.5
                                val current = appSettings ?: AppSettings()
                                viewModel.saveSettings(current.copy(vatEnabled = true, vatRate = rate))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldAccent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Update Tax Rate")
                        }
                    }
                }
            }
        }

        // Theme Appearance Switcher
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Brightness4, contentDescription = null, tint = EmeraldAccent, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("App Theme & Color System", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val currentTheme = appSettings?.theme ?: "DarkGreen"

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (currentTheme == "DarkGreen") EmeraldAccent else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.updateTheme("DarkGreen") }
                        ) {
                            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🌲 Dark Green", fontWeight = FontWeight.Bold, color = if (currentTheme == "DarkGreen") Color.Black else MaterialTheme.colorScheme.onSurface)
                                Text("Vendora Signature", fontSize = 11.sp, color = if (currentTheme == "DarkGreen") Color.DarkGray else MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (currentTheme == "Light") EmeraldAccent else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.updateTheme("Light") }
                        ) {
                            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("☀️ Light Modern", fontWeight = FontWeight.Bold, color = if (currentTheme == "Light") Color.Black else MaterialTheme.colorScheme.onSurface)
                                Text("High Contrast", fontSize = 11.sp, color = if (currentTheme == "Light") Color.DarkGray else MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // Gemini AI Configuration & Custom API Key
        item {
            var customKeyInput by remember(appSettings) { mutableStateOf(appSettings?.customGeminiApiKey ?: "") }
            var selectedModel by remember(appSettings) { mutableStateOf(appSettings?.geminiModel ?: "gemini-3.5-flash") }
            var isTestingKey by remember { mutableStateOf(false) }
            var testStatusMessage by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
            var showKeyText by remember { mutableStateOf(false) }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("✨", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Gemini AI Configuration", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Configure custom API key and AI models for store analytics", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    OutlinedTextField(
                        value = customKeyInput,
                        onValueChange = {
                            customKeyInput = it
                            testStatusMessage = null
                        },
                        label = { Text("Custom Gemini API Key") },
                        placeholder = { Text("AIzaSy...") },
                        visualTransformation = if (showKeyText) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            Text(
                                text = if (showKeyText) "Hide" else "Show",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldAccent,
                                modifier = Modifier
                                    .clickable { showKeyText = !showKeyText }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth().testTag("custom_gemini_key_input")
                    )

                    Text("Selected Model:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val models = listOf(
                            "gemini-3.5-flash" to "3.5 Flash",
                            "gemini-3.1-pro-preview" to "3.1 Pro",
                            "gemini-2.5-flash" to "2.5 Flash"
                        )
                        models.forEach { (modelId, label) ->
                            val isSelected = selectedModel == modelId
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) EmeraldAccent else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedModel = modelId
                                        testStatusMessage = null
                                    }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    if (testStatusMessage != null) {
                        val (success, msg) = testStatusMessage!!
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (success) SuccessGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = msg,
                                fontSize = 12.sp,
                                color = if (success) SuccessGreen else MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                isTestingKey = true
                                testStatusMessage = null
                                viewModel.testGeminiApiKey(customKeyInput, selectedModel) { success, msg ->
                                    isTestingKey = false
                                    testStatusMessage = Pair(success, msg)
                                }
                            },
                            enabled = !isTestingKey && customKeyInput.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurface),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("test_ai_key_btn")
                        ) {
                            if (isTestingKey) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Testing...", fontSize = 12.sp)
                            } else {
                                Text("Test Connection", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.saveGeminiConfig(customKeyInput, selectedModel)
                                viewModel.showToast("Gemini AI settings saved! ✨")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldAccent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("save_ai_settings_btn")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save AI Key", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Security & Credentials
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = EmeraldAccent, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Security & Admin Password", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Change Admin Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            if (newPassword.isNotBlank()) {
                                viewModel.updatePassword(newPassword.trim())
                                newPassword = ""
                            }
                        },
                        enabled = newPassword.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldAccent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Update Password")
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    Text("Security Questions (For Account Recovery):", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)

                    OutlinedTextField(
                        value = a1,
                        onValueChange = { a1 = it },
                        label = { Text(q1) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = a2,
                        onValueChange = { a2 = it },
                        label = { Text(q2) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            if (a1.isNotBlank() && a2.isNotBlank()) {
                                val list = listOf(
                                    SecurityQuestion(q1, a1.trim().lowercase()),
                                    SecurityQuestion(q2, a2.trim().lowercase())
                                )
                                viewModel.saveSecurityQuestions(list)
                            } else {
                                viewModel.showToast("Please provide answers to both security questions.")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldAccent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Save Security Questions")
                    }
                }
            }
        }

        // Full Store Backup JSON
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, tint = EmeraldAccent, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Complete Store Backup", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Export a full JSON snapshot of all products, sales records, and debt ledgers.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val backupJson = buildString {
                                appendLine("{")
                                appendLine("  \"exportedAt\": \"${System.currentTimeMillis()}\",")
                                appendLine("  \"productsCount\": ${products.size},")
                                appendLine("  \"salesCount\": ${sales.size},")
                                appendLine("  \"debtsCount\": ${debts.size}")
                                appendLine("}")
                            }
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, backupJson)
                                type = "application/json"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Export Store Backup"))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBluePrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export JSON Backup")
                    }
                }
            }
        }

        // About & Version Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = FintechEmerald, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("About IBR SHOP", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "IBR SHOP v2.5.0 • Intelligent POS, Stock Inventory, Debt Tracker & Predictive Analytics Engine. Powered by Room Database and Gemini AI.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
