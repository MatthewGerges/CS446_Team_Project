package com.example.stellar.ui.balance

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Temporary in-memory store for balance data until the backend is integrated.
 */
object BalanceRepository {

    private val _uiState = MutableStateFlow(BalanceUiState())
    val uiState: StateFlow<BalanceUiState> = _uiState.asStateFlow()

    /**
     * Applies a shared expense to the current balances by splitting [totalAmount]
     * equally across all [participantIds].
     *
     * Positive amounts mean "this person owes you", negative means "you owe them".
     */
    fun applySharedExpense(totalAmount: Double, participantIds: Set<String>) {
        if (totalAmount <= 0.0 || participantIds.isEmpty()) return

        val current = _uiState.value
        val share = totalAmount / participantIds.size

        val updatedBalances = current.balances.map { item ->
            if (item.id in participantIds) {
                item.copy(amount = item.amount + share)
            } else {
                item
            }
        }

        _uiState.value = current.copy(balances = updatedBalances)
    }
}

