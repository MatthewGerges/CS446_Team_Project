package com.example.stellar.data.model

data class BalanceItem(
    val id: String,
    val personName: String,
    val amount: Double // positive = they owe you, negative = you owe them
)
