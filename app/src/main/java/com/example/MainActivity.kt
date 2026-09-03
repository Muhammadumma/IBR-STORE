package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.navigation.VendoraScreen
import com.example.ui.screens.AiAnalyticsScreen
import com.example.ui.screens.BulkAddProductScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DebtManagerScreen
import com.example.ui.screens.GlobalSearchScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.PosScreen
import com.example.ui.screens.PurchasesScreen
import com.example.ui.screens.ReceiptDialog
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.BrandBluePrimary
import com.example.ui.theme.DangerRed
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.FintechEmerald
import com.example.ui.theme.VendoraTheme
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.VendoraViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: VendoraViewModel = viewModel()
            val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()
            val isDarkTheme = appSettings?.theme.equals("dark", ignoreCase = true) || appSettings?.theme == "DarkGreen"

            VendoraTheme(isDarkTheme = isDarkTheme) {
                VendoraApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendoraApp(viewModel: VendoraViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val cart by viewModel.cart.collectAsStateWithLifecycle()
    val activeReceiptSale by viewModel.activeReceiptSale.collectAsStateWithLifecycle()
    val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val debts by viewModel.debts.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearToast()
        }
    }

    val lowStockCount = remember(products) { products.count { it.qty < 5 } }
    val unpaidDebtsCount = remember(debts) { debts.count { it.status != "Paid" } }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.width(300.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Drawer Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = FintechEmerald.copy(alpha = 0.12f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Storefront,
                                    contentDescription = null,
                                    tint = FintechEmerald,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = appSettings?.businessName ?: "IBR SHOP",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Intelligent POS & Inventory",
                                fontSize = 11.sp,
                                color = FintechEmerald,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)

                    // Menu Items
                    val menuItems = listOf(
                        VendoraScreen.Dashboard,
                        VendoraScreen.Sell,
                        VendoraScreen.Inventory,
                        VendoraScreen.AddProducts,
                        VendoraScreen.Purchases,
                        VendoraScreen.DebtManager,
                        VendoraScreen.AiAnalytics,
                        VendoraScreen.Reports,
                        VendoraScreen.Settings
                    )

                    menuItems.forEach { screen ->
                        val isSelected = currentScreen == screen
                        val badgeText = when (screen) {
                            VendoraScreen.Inventory -> if (lowStockCount > 0) "$lowStockCount Low" else null
                            VendoraScreen.DebtManager -> if (unpaidDebtsCount > 0) "$unpaidDebtsCount Due" else null
                            VendoraScreen.Sell -> if (cart.isNotEmpty()) "${cart.sumOf { it.qty }}" else null
                            else -> null
                        }

                        NavigationDrawerItem(
                            label = { Text(screen.title, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            selected = isSelected,
                            badge = if (badgeText != null) {
                                {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (screen == VendoraScreen.DebtManager) WarningAmber else (if (screen == VendoraScreen.Inventory) DangerRed else FintechEmerald)
                                    ) {
                                        Text(
                                            badgeText,
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            } else null,
                            onClick = {
                                viewModel.navigateTo(screen)
                                scope.launch { drawerState.close() }
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = FintechEmerald.copy(alpha = 0.12f),
                                selectedIconColor = FintechEmerald,
                                selectedTextColor = FintechEmerald
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.padding(vertical = 2.dp).testTag("drawer_item_${screen.name.lowercase()}")
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Bottom info
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                Icons.Default.Dashboard,
                                contentDescription = null,
                                tint = FintechEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text("Offline First", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("Room Database + Gemini AI", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = currentScreen.title,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.testTag("open_drawer_btn")
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.navigateTo(VendoraScreen.Search) },
                            modifier = Modifier.testTag("top_search_btn")
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }

                        // Cart Action
                        IconButton(
                            onClick = { viewModel.navigateTo(VendoraScreen.Sell) },
                            modifier = Modifier.testTag("top_cart_btn")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (cart.isNotEmpty()) {
                                        Badge(containerColor = EmeraldAccent) {
                                            Text("${cart.sumOf { it.qty }}", color = Color.Black, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "POS Cart")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            },
            bottomBar = {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 10.dp
                ) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 0.dp
                    ) {
                        val bottomItems = listOf(
                            VendoraScreen.Dashboard,
                            VendoraScreen.Sell,
                            VendoraScreen.Inventory,
                            VendoraScreen.DebtManager,
                            VendoraScreen.AiAnalytics
                        )

                        bottomItems.forEach { screen ->
                            val isSelected = currentScreen == screen
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.navigateTo(screen) },
                                icon = {
                                    if (screen == VendoraScreen.Sell && cart.isNotEmpty()) {
                                        BadgedBox(
                                            badge = {
                                                Badge(containerColor = EmeraldAccent) {
                                                    Text("${cart.sumOf { it.qty }}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                                }
                                            }
                                        ) {
                                            Icon(screen.icon, contentDescription = screen.title)
                                        }
                                    } else {
                                        Icon(screen.icon, contentDescription = screen.title)
                                    }
                                },
                                label = {
                                    val bottomLabel = when (screen) {
                                        VendoraScreen.Dashboard -> "Home"
                                        VendoraScreen.Sell -> "POS"
                                        VendoraScreen.Inventory -> "Stock"
                                        VendoraScreen.DebtManager -> "Debts"
                                        VendoraScreen.AiAnalytics -> "Insights"
                                        else -> screen.title
                                    }
                                    Text(
                                        bottomLabel,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        maxLines = 1
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = FintechEmerald,
                                    selectedTextColor = FintechEmerald,
                                    indicatorColor = FintechEmerald.copy(alpha = 0.12f),
                                    unselectedIconColor = Color(0xFF94A3B8),
                                    unselectedTextColor = Color(0xFF94A3B8)
                                ),
                                modifier = Modifier.testTag("bottom_nav_${screen.name.lowercase()}")
                            )
                        }
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "ScreenTransition"
                ) { screen ->
                    when (screen) {
                        VendoraScreen.Dashboard -> DashboardScreen(viewModel = viewModel)
                        VendoraScreen.Inventory -> InventoryScreen(viewModel = viewModel)
                        VendoraScreen.AddProducts -> BulkAddProductScreen(viewModel = viewModel)
                        VendoraScreen.Sell -> PosScreen(viewModel = viewModel)
                        VendoraScreen.Purchases -> PurchasesScreen(viewModel = viewModel)
                        VendoraScreen.DebtManager -> DebtManagerScreen(viewModel = viewModel)
                        VendoraScreen.AiAnalytics -> AiAnalyticsScreen(viewModel = viewModel)
                        VendoraScreen.Reports -> ReportsScreen(viewModel = viewModel)
                        VendoraScreen.Settings -> SettingsScreen(viewModel = viewModel)
                        VendoraScreen.Search -> GlobalSearchScreen(viewModel = viewModel)
                        VendoraScreen.Auth -> SettingsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }

    // Digital Receipt Modal
    if (activeReceiptSale != null) {
        ReceiptDialog(
            sale = activeReceiptSale!!,
            settings = appSettings,
            viewModel = viewModel,
            onDismiss = { viewModel.closeReceipt() }
        )
    }
}
