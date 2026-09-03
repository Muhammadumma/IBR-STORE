package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiService
import com.example.data.model.AppSettings
import com.example.data.model.AuditLog
import com.example.data.model.CartItem
import com.example.data.model.Debt
import com.example.data.model.Product
import com.example.data.model.Purchase
import com.example.data.model.Sale
import com.example.data.model.SecurityQuestion
import com.example.data.model.UserProfile
import com.example.data.repository.VendoraRepository
import com.example.ui.navigation.VendoraScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class VendoraViewModel(application: Application) : AndroidViewModel(application) {
    val repository = VendoraRepository(application)

    val products: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sales: StateFlow<List<Sale>> = repository.allSales
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val purchases: StateFlow<List<Purchase>> = repository.allPurchases
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val debts: StateFlow<List<Debt>> = repository.allDebts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val logs: StateFlow<List<AuditLog>> = repository.allLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val appSettings: StateFlow<AppSettings?> = repository.appSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Current Screen
    private val _currentScreen = MutableStateFlow(VendoraScreen.Dashboard)
    val currentScreen: StateFlow<VendoraScreen> = _currentScreen.asStateFlow()

    // POS Cart
    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    // Global Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // AI
    private val _aiResponse = MutableStateFlow<String?>(null)
    val aiResponse: StateFlow<String?> = _aiResponse.asStateFlow()
    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    // Active Receipt Modal
    private val _activeReceiptSale = MutableStateFlow<Sale?>(null)
    val activeReceiptSale: StateFlow<Sale?> = _activeReceiptSale.asStateFlow()

    // Toast/Snackbar Message
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    fun navigateTo(screen: VendoraScreen) {
        _currentScreen.value = screen
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isNotBlank() && _currentScreen.value != VendoraScreen.Search) {
            _currentScreen.value = VendoraScreen.Search
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    // --- Cart Actions ---
    fun addToCart(product: Product) {
        if (product.qty <= 0) {
            showToast("Product is out of stock!")
            return
        }
        val current = _cart.value.toMutableList()
        val index = current.indexOfFirst { it.productId == product.id }
        if (index != -1) {
            val existing = current[index]
            if (existing.qty < product.qty) {
                current[index] = existing.copy(qty = existing.qty + 1)
            } else {
                showToast("Cannot add more: Max available stock reached (${product.qty})")
                return
            }
        } else {
            current.add(
                CartItem(
                    productId = product.id,
                    name = product.name,
                    price = product.sellPrice,
                    buyPrice = product.buyPrice,
                    qty = 1
                )
            )
        }
        _cart.value = current
    }

    fun updateCartItemQty(productId: String, newQty: Int, maxStock: Int) {
        val current = _cart.value.toMutableList()
        val index = current.indexOfFirst { it.productId == productId }
        if (index != -1) {
            if (newQty <= 0) {
                current.removeAt(index)
            } else if (newQty > maxStock) {
                showToast("Only $maxStock items in stock.")
                current[index] = current[index].copy(qty = maxStock)
            } else {
                current[index] = current[index].copy(qty = newQty)
            }
            _cart.value = current
        }
    }

    fun removeFromCart(productId: String) {
        _cart.value = _cart.value.filter { it.productId != productId }
    }

    fun clearCart() {
        _cart.value = emptyList()
    }

    fun checkout(
        paymentMethod: String,
        customerName: String,
        customerPhone: String,
        dueDate: String?,
        onSuccess: (Sale) -> Unit
    ) {
        val currentCart = _cart.value
        if (currentCart.isEmpty()) {
            showToast("Cart is empty!")
            return
        }
        val vatRate = if (appSettings.value?.vatEnabled == true) appSettings.value?.vatRate ?: 0.0 else 0.0

        viewModelScope.launch {
            val result = repository.addSale(
                items = currentCart,
                paymentMethod = paymentMethod,
                customerName = customerName,
                customerPhone = customerPhone,
                dueDate = dueDate,
                vatRate = vatRate
            )
            result.onSuccess { sale ->
                clearCart()
                _activeReceiptSale.value = sale
                showToast("Sale recorded successfully! ✅")
                onSuccess(sale)
            }.onFailure { error ->
                showToast(error.message ?: "Failed to process sale.")
            }
        }
    }

    fun closeReceipt() {
        _activeReceiptSale.value = null
    }

    fun showReceiptForSale(sale: Sale) {
        _activeReceiptSale.value = sale
    }

    // --- Product Actions ---
    fun addProduct(product: Product, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.addProduct(product)
            showToast("Product '${product.name}' added! 📦")
            onSuccess()
        }
    }

    fun bulkAddProducts(productsList: List<Product>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.addProductsBulk(productsList)
            showToast("Successfully added ${productsList.size} products! 📦")
            onSuccess()
        }
    }

    fun updateProduct(product: Product, oldProduct: Product, user: String) {
        viewModelScope.launch {
            repository.updateProduct(product)
            repository.logAudit(
                productName = product.name,
                action = "Update",
                oldQty = oldProduct.qty.toString(),
                newQty = product.qty.toString(),
                oldPrice = formatCurrency(oldProduct.sellPrice),
                newPrice = formatCurrency(product.sellPrice),
                user = user
            )
            showToast("Product updated and logged.")
        }
    }

    fun deleteProduct(id: String) {
        viewModelScope.launch {
            repository.deleteProduct(id)
            showToast("Product deleted.")
        }
    }

    // --- Purchases ---
    fun addPurchase(productId: String, productName: String, qty: Int, buyPrice: Double, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.addPurchase(productId, productName, qty, buyPrice)
            showToast("Restocked $qty units of $productName! 🚚")
            onSuccess()
        }
    }

    // --- Debts ---
    fun addDebtManual(debtor: String, phone: String, product: String, amount: Double, dueDate: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = repository.addDebtManual(debtor, phone, product, amount, dueDate)
            result.onSuccess {
                showToast("Debt recorded successfully!")
                onSuccess()
            }.onFailure { error ->
                showToast(error.message ?: "Error recording debt.")
            }
        }
    }

    fun addOverdueReason(debtId: String, reason: String) {
        viewModelScope.launch {
            repository.addOverdueReason(debtId, reason)
            showToast("Overdue reason recorded.")
        }
    }

    fun settleDebt(debtId: String) {
        viewModelScope.launch {
            repository.settleDebt(debtId)
            showToast("Debt marked as settled and logged to cash flow! 💰")
        }
    }

    // --- Settings & Auth ---
    fun saveSettings(settings: AppSettings) {
        viewModelScope.launch {
            repository.updateSettings(settings)
            showToast("Settings saved successfully! ⚙️")
        }
    }

    fun updateTheme(themeName: String) {
        val current = appSettings.value ?: AppSettings()
        saveSettings(current.copy(theme = themeName))
    }

    fun updatePassword(newPass: String) {
        val current = userProfile.value ?: UserProfile()
        viewModelScope.launch {
            repository.updateUserProfile(current.copy(password = newPass))
            showToast("Password updated successfully! 🔒")
        }
    }

    fun saveSecurityQuestions(questions: List<SecurityQuestion>) {
        val current = userProfile.value ?: UserProfile()
        val json = repository.serializeSecurityQuestions(questions)
        viewModelScope.launch {
            repository.updateUserProfile(current.copy(securityQuestionsJson = json))
            showToast("Security questions saved! 🛡️")
        }
    }

    fun login(user: String, pass: String, onResult: (Boolean) -> Unit) {
        val profile = userProfile.value
        if (profile != null && profile.password == pass && profile.username.equals(user, ignoreCase = true)) {
            showToast("Welcome back, ${profile.username}!")
            onResult(true)
        } else if (profile == null && pass == "1234") {
            onResult(true)
        } else {
            showToast("Invalid credentials.")
            onResult(false)
        }
    }

    // --- AI Analytics ---
    fun askGemini(query: String) {
        if (query.isBlank()) return
        _isAiLoading.value = true
        _aiResponse.value = null

        val currentSales = sales.value
        val currentProducts = products.value
        val currentDebts = debts.value
        val settings = appSettings.value

        val totalRev = currentSales.sumOf { it.total }
        val totalProfit = currentSales.sumOf { it.profit }
        val stockVal = currentProducts.sumOf { it.qty * it.buyPrice }
        val activeDebt = currentDebts.filter { it.status != "Paid" }.sumOf { it.amount }

        val context = """
            Total Sales Count: ${currentSales.size}
            Total Revenue: ₦$totalRev
            Total Gross Profit: ₦$totalProfit
            Total Inventory Value: ₦$stockVal
            Active Unpaid Debt: ₦$activeDebt
            Product Inventory Count: ${currentProducts.size} items
            Top Products: ${currentProducts.take(8).joinToString { "${it.name} (Stock: ${it.qty}, Sell: ₦${it.sellPrice})" }}
        """.trimIndent()

        viewModelScope.launch {
            val response = GeminiService.askGemini(
                prompt = query,
                businessContext = context,
                customApiKey = settings?.customGeminiApiKey,
                modelName = settings?.geminiModel ?: "gemini-3.5-flash"
            )
            _aiResponse.value = response
            _isAiLoading.value = false
        }
    }

    fun testGeminiApiKey(apiKey: String, model: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = GeminiService.testApiKey(apiKey, model)
            onResult(result.first, result.second)
        }
    }

    fun saveGeminiConfig(customKey: String, model: String) {
        val current = appSettings.value ?: AppSettings()
        saveSettings(current.copy(customGeminiApiKey = customKey.trim(), geminiModel = model.trim()))
    }

    // --- Helpers ---
    fun formatCurrency(amount: Double): String {
        return try {
            val format = NumberFormat.getCurrencyInstance(Locale("en", "NG"))
            format.currency = java.util.Currency.getInstance("NGN")
            format.format(amount)
        } catch (_: Exception) {
            "₦" + String.format(Locale.getDefault(), "%,.2f", amount)
        }
    }
}
