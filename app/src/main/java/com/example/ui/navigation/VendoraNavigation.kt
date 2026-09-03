package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

enum class VendoraScreen(val title: String, val icon: ImageVector) {
    Dashboard("Dashboard", Icons.Default.Dashboard),
    Inventory("Inventory", Icons.Default.Inventory2),
    AddProducts("Add Products", Icons.Default.PlaylistAdd),
    Sell("Point of Sale", Icons.Default.ShoppingCart),
    Purchases("Purchases", Icons.Default.LocalShipping),
    DebtManager("Debt Manager", Icons.Default.MoneyOff),
    AiAnalytics("AI Analytics", Icons.Default.BarChart),
    Reports("Reports", Icons.Default.Description),
    Settings("Settings", Icons.Default.Settings),
    Search("Search", Icons.Default.Search),
    Auth("Sign In", Icons.Default.Settings)
}
