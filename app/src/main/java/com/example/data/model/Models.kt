package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: String = System.currentTimeMillis().toString(),
    val name: String,
    val category: String = "General",
    val qty: Int = 0,
    val buyPrice: Double = 0.0,
    val sellPrice: Double = 0.0,
    val createdAt: String = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
)

data class CartItem(
    val productId: String,
    val name: String,
    val price: Double,
    val buyPrice: Double,
    val qty: Int
)

@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey val id: String = "SALE-" + System.currentTimeMillis(),
    val date: String = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date()),
    val customerName: String = "Walk-in Customer",
    val customerPhone: String = "",
    val paymentMethod: String = "Cash", // Cash, Store Bank, POS, Debt, Debt Settlement
    val itemsJson: String = "[]", // Serialized List<CartItem>
    val subtotal: Double = 0.0,
    val vat: Double = 0.0,
    val total: Double = 0.0,
    val profit: Double = 0.0,
    val dueDate: String? = null,
    val isSettlement: Boolean = false
)

@Entity(tableName = "purchases")
data class Purchase(
    @PrimaryKey val id: String = "PUR-" + System.currentTimeMillis(),
    val productId: String,
    val productName: String,
    val qty: Int,
    val buyPrice: Double,
    val date: String = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
)

data class OverdueReason(
    val reason: String,
    val date: String
)

@Entity(tableName = "debts")
data class Debt(
    @PrimaryKey val id: String = "DEBT-" + System.currentTimeMillis(),
    val debtor: String,
    val phone: String = "",
    val product: String,
    val amount: Double,
    val dueDate: String,
    val createdAt: String = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date()),
    val status: String = "Pending", // Pending, Paid
    val settledDate: String? = null,
    val overdueReasonsJson: String = "[]", // Serialized List<OverdueReason>
    val isPermanent: Boolean = true,
    val archived: Boolean = false,
    val sourceSaleId: String? = null
)

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey val id: String = "LOG-" + System.currentTimeMillis(),
    val timestamp: String = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date()),
    val user: String = "Admin",
    val action: String = "Update",
    val productName: String = "",
    val oldQty: String = "-",
    val newQty: String = "-",
    val oldPrice: String = "-",
    val newPrice: String = "-"
)

data class SecurityQuestion(
    val question: String,
    val answer: String
)

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val username: String = "Admin",
    val password: String = "1234",
    val role: String = "Shop Manager",
    val profilePicUri: String? = null,
    val securityQuestionsJson: String? = null
)

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey val id: Int = 1,
    val businessName: String = "IBR SHOP",
    val businessAddress: String = "123 Business Street, Lagos",
    val businessPhone: String = "+234 800 000 0000",
    val receiptFooter: String = "Thank you for your patronage! Goods bought in good condition are not returnable.",
    val vatEnabled: Boolean = true,
    val vatRate: Double = 15.0,
    val theme: String = "Light", // "Light" (Clean Minimalist Fintech) or "Dark"
    val customGeminiApiKey: String = "",
    val geminiModel: String = "gemini-3.5-flash"
)
