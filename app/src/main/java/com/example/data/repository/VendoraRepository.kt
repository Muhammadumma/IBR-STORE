package com.example.data.repository

import android.content.Context
import androidx.room.Room
import com.example.data.local.VendoraDatabase
import com.example.data.model.AppSettings
import com.example.data.model.AuditLog
import com.example.data.model.CartItem
import com.example.data.model.Debt
import com.example.data.model.OverdueReason
import com.example.data.model.Product
import com.example.data.model.Purchase
import com.example.data.model.Sale
import com.example.data.model.SecurityQuestion
import com.example.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VendoraRepository(context: Context) {
    private val db = Room.databaseBuilder(
        context.applicationContext,
        VendoraDatabase::class.java,
        "vendora_shop.db"
    ).fallbackToDestructiveMigration().build()

    private val productDao = db.productDao()
    private val saleDao = db.saleDao()
    private val purchaseDao = db.purchaseDao()
    private val debtDao = db.debtDao()
    private val auditLogDao = db.auditLogDao()
    private val userProfileDao = db.userProfileDao()
    private val appSettingsDao = db.appSettingsDao()

    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    val allSales: Flow<List<Sale>> = saleDao.getAllSales()
    val allPurchases: Flow<List<Purchase>> = purchaseDao.getAllPurchases()
    val allDebts: Flow<List<Debt>> = debtDao.getAllDebts()
    val allLogs: Flow<List<AuditLog>> = auditLogDao.getAllLogs()
    val userProfile: Flow<UserProfile?> = userProfileDao.getUserProfile()
    val appSettings: Flow<AppSettings?> = appSettingsDao.getSettings()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            // Ensure default settings exist
            if (appSettingsDao.getSettingsOnce() == null) {
                appSettingsDao.saveSettings(AppSettings())
            }
            // Ensure default user exists
            if (userProfileDao.getUserProfileOnce() == null) {
                userProfileDao.saveUserProfile(UserProfile())
            }
            // Seed initial sample inventory if totally empty
            val currentProducts = productDao.getAllProducts().firstOrNull()
            if (currentProducts.isNullOrEmpty()) {
                seedInitialProducts()
            }
        }
    }

    private suspend fun seedInitialProducts() {
        val sampleList = listOf(
            Product(id = "1", name = "Smart Watch X200", category = "Electronics", qty = 18, buyPrice = 25000.0, sellPrice = 38000.0),
            Product(id = "2", name = "Wireless Earbuds Pro", category = "Electronics", qty = 12, buyPrice = 12000.0, sellPrice = 19500.0),
            Product(id = "3", name = "Leather Travel Backpack", category = "Fashion", qty = 4, buyPrice = 15000.0, sellPrice = 26000.0),
            Product(id = "4", name = "Organic Green Tea (Pack)", category = "Groceries", qty = 35, buyPrice = 2200.0, sellPrice = 3500.0),
            Product(id = "5", name = "Fast Charging PowerBank 20k", category = "Electronics", qty = 3, buyPrice = 14000.0, sellPrice = 22000.0),
            Product(id = "6", name = "Stainless Steel Water Flask", category = "Home & Living", qty = 24, buyPrice = 4500.0, sellPrice = 8000.0)
        )
        productDao.insertAll(sampleList)
    }

    suspend fun addProduct(product: Product) {
        productDao.insertProduct(product)
    }

    suspend fun addProductsBulk(products: List<Product>) {
        productDao.insertAll(products)
    }

    suspend fun updateProduct(product: Product) {
        productDao.updateProduct(product)
    }

    suspend fun deleteProduct(id: String) {
        productDao.deleteProductById(id)
    }

    suspend fun addSale(
        items: List<CartItem>,
        paymentMethod: String,
        customerName: String,
        customerPhone: String,
        dueDate: String?,
        vatRate: Double
    ): Result<Sale> {
        val currentProducts = productDao.getAllProducts().firstOrNull() ?: emptyList()
        val totalStockValue = currentProducts.sumOf { it.qty * it.buyPrice }
        val currentDebts = debtDao.getAllDebts().firstOrNull() ?: emptyList()
        val activeDebt = currentDebts.filter { it.status != "Paid" }.sumOf { it.amount }

        val subtotal = items.sumOf { it.price * it.qty }
        val vat = if (vatRate > 0) subtotal * (vatRate / 100.0) else 0.0
        val total = subtotal + vat
        val profit = items.sumOf { (it.price - it.buyPrice) * it.qty }

        // Enforce 30% Debt Cap if payment method is Debt
        if (paymentMethod == "Debt") {
            if (totalStockValue > 0 && (activeDebt + total) > (totalStockValue * 0.30)) {
                return Result.failure(Exception("Debt Limit Exceeded! Active debt cannot exceed 30% of total stock value (₦${String.format(Locale.getDefault(), "%,.2f", totalStockValue * 0.30)})."))
            }
        }

        val itemsJson = JSONArray().apply {
            items.forEach { item ->
                put(JSONObject().apply {
                    put("productId", item.productId)
                    put("name", item.name)
                    put("price", item.price)
                    put("buyPrice", item.buyPrice)
                    put("qty", item.qty)
                })
            }
        }.toString()

        val saleId = "SALE-" + System.currentTimeMillis()
        val sale = Sale(
            id = saleId,
            customerName = customerName.ifBlank { "Walk-in Customer" },
            customerPhone = customerPhone,
            paymentMethod = paymentMethod,
            itemsJson = itemsJson,
            subtotal = subtotal,
            vat = vat,
            total = total,
            profit = profit,
            dueDate = dueDate,
            isSettlement = false
        )
        saleDao.insertSale(sale)

        // Deduct inventory stock
        for (item in items) {
            val prod = productDao.getProductById(item.productId)
            if (prod != null) {
                val newQty = (prod.qty - item.qty).coerceAtLeast(0)
                productDao.updateProduct(prod.copy(qty = newQty))
            }
        }

        // If Debt, record debt entry
        if (paymentMethod == "Debt") {
            val debt = Debt(
                id = "DEBT-" + System.currentTimeMillis(),
                debtor = customerName.ifBlank { "Unknown Customer" },
                phone = customerPhone,
                product = "Sale #${saleId.takeLast(6)} (${items.size} items)",
                amount = total,
                dueDate = dueDate ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000)),
                status = "Pending",
                sourceSaleId = saleId
            )
            debtDao.insertDebt(debt)
        }

        return Result.success(sale)
    }

    suspend fun addPurchase(productId: String, productName: String, qty: Int, buyPrice: Double) {
        val purchase = Purchase(
            productId = productId,
            productName = productName,
            qty = qty,
            buyPrice = buyPrice
        )
        purchaseDao.insertPurchase(purchase)

        val prod = productDao.getProductById(productId)
        if (prod != null) {
            val updatedQty = prod.qty + qty
            productDao.updateProduct(prod.copy(qty = updatedQty, buyPrice = buyPrice))
        }
    }

    suspend fun addDebtManual(debtor: String, phone: String, product: String, amount: Double, dueDate: String): Result<Debt> {
        val currentProducts = productDao.getAllProducts().firstOrNull() ?: emptyList()
        val totalStockValue = currentProducts.sumOf { it.qty * it.buyPrice }
        val currentDebts = debtDao.getAllDebts().firstOrNull() ?: emptyList()
        val activeDebt = currentDebts.filter { it.status != "Paid" }.sumOf { it.amount }

        if (totalStockValue > 0 && (activeDebt + amount) > (totalStockValue * 0.30)) {
            return Result.failure(Exception("Cannot add debt: Exceeds 30% cap of total stock value."))
        }

        val debt = Debt(
            debtor = debtor,
            phone = phone,
            product = product,
            amount = amount,
            dueDate = dueDate,
            status = "Pending"
        )
        debtDao.insertDebt(debt)
        return Result.success(debt)
    }

    suspend fun addOverdueReason(debtId: String, reason: String) {
        val debt = debtDao.getDebtById(debtId) ?: return
        val currentReasons = parseOverdueReasons(debt.overdueReasonsJson).toMutableList()
        currentReasons.add(OverdueReason(reason, SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())))
        val newReasonsJson = JSONArray().apply {
            currentReasons.forEach { r ->
                put(JSONObject().apply {
                    put("reason", r.reason)
                    put("date", r.date)
                })
            }
        }.toString()
        debtDao.updateDebt(debt.copy(overdueReasonsJson = newReasonsJson))
    }

    suspend fun settleDebt(debtId: String) {
        val debt = debtDao.getDebtById(debtId) ?: return
        val settledDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())

        debtDao.updateDebt(debt.copy(status = "Paid", settledDate = settledDate))

        // Create settlement sale record for cash flow tracking
        val itemJson = JSONArray().apply {
            put(JSONObject().apply {
                put("productId", "DEBT-SETTLE")
                put("name", "Debt Settlement: ${debt.product}")
                put("price", debt.amount)
                put("buyPrice", 0.0)
                put("qty", 1)
            })
        }.toString()

        val settlementSale = Sale(
            id = "SETTLE-" + System.currentTimeMillis(),
            date = settledDate,
            customerName = debt.debtor,
            customerPhone = debt.phone,
            paymentMethod = "Debt Settlement",
            itemsJson = itemJson,
            subtotal = debt.amount,
            vat = 0.0,
            total = debt.amount,
            profit = debt.amount,
            isSettlement = true
        )
        saleDao.insertSale(settlementSale)
    }

    suspend fun logAudit(productName: String, action: String, oldQty: String, newQty: String, oldPrice: String, newPrice: String, user: String) {
        val log = AuditLog(
            user = user,
            action = action,
            productName = productName,
            oldQty = oldQty,
            newQty = newQty,
            oldPrice = oldPrice,
            newPrice = newPrice
        )
        auditLogDao.insertLog(log)
    }

    suspend fun updateSettings(settings: AppSettings) {
        appSettingsDao.saveSettings(settings)
    }

    suspend fun updateUserProfile(profile: UserProfile) {
        userProfileDao.saveUserProfile(profile)
    }

    fun parseCartItems(itemsJson: String): List<CartItem> {
        val list = mutableListOf<CartItem>()
        try {
            val array = JSONArray(itemsJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    CartItem(
                        productId = obj.optString("productId"),
                        name = obj.optString("name"),
                        price = obj.optDouble("price", 0.0),
                        buyPrice = obj.optDouble("buyPrice", 0.0),
                        qty = obj.optInt("qty", 1)
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun parseOverdueReasons(json: String): List<OverdueReason> {
        val list = mutableListOf<OverdueReason>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    OverdueReason(
                        reason = obj.optString("reason"),
                        date = obj.optString("date")
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun parseSecurityQuestions(json: String?): List<SecurityQuestion> {
        if (json.isNullOrBlank()) return emptyList()
        val list = mutableListOf<SecurityQuestion>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    SecurityQuestion(
                        question = obj.optString("question"),
                        answer = obj.optString("answer")
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun serializeSecurityQuestions(list: List<SecurityQuestion>): String {
        return JSONArray().apply {
            list.forEach { q ->
                put(JSONObject().apply {
                    put("question", q.question)
                    put("answer", q.answer.trim().lowercase())
                })
            }
        }.toString()
    }

    suspend fun generateFullBackupJson(): String {
        val products = productDao.getAllProducts().firstOrNull() ?: emptyList()
        val sales = saleDao.getAllSales().firstOrNull() ?: emptyList()
        val purchases = purchaseDao.getAllPurchases().firstOrNull() ?: emptyList()
        val debts = debtDao.getAllDebts().firstOrNull() ?: emptyList()
        val logs = auditLogDao.getAllLogs().firstOrNull() ?: emptyList()
        val settings = appSettingsDao.getSettingsOnce() ?: AppSettings()

        val root = JSONObject().apply {
            put("version", "2.4.1")
            put("appName", "Vendora")
            put("exportedAt", SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date()))
            put("productsCount", products.size)
            put("salesCount", sales.size)
            put("debtsCount", debts.size)
            put("businessName", settings.businessName)
        }
        return root.toString(2)
    }
}
