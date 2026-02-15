package com.example.stellar.ui.balance

import androidx.lifecycle.ViewModel
import com.example.stellar.data.model.BalanceItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class BalanceUiState(
    val balances: List<BalanceItem> = listOf(
        BalanceItem("1", "Alice", -25.50),
        BalanceItem("2", "Bob", 15.00),
        BalanceItem("3", "Charlie", -10.00),
        BalanceItem("4", "Diana", 32.75)
    )
) {
    val youOwe: List<BalanceItem> get() = balances.filter { it.amount < 0 }
    val owedToYou: List<BalanceItem> get() = balances.filter { it.amount > 0 }
    val netBalance: Double get() = balances.sumOf { it.amount }
}

class BalanceViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BalanceUiState())
    val uiState: StateFlow<BalanceUiState> = _uiState.asStateFlow()
}
