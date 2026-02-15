package com.example.stellar.data.model

enum class ReceiptStatus { PENDING, CONFIRMED }

data class Receipt(
    val id: String,
    val storeName: String,
    val amount: Double,
    val date: String,
    val status: ReceiptStatus = ReceiptStatus.PENDING
)
