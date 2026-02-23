package com.example.stellar.ui.balance

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * Temporary in-memory store for balance data until the backend is integrated.
 */
data class SettlementRecord(
    val id: String,
    val personId: String,
    val personName: String,
    val amount: Double,
    val method: String,
    val timestampMillis: Long
)

object BalanceRepository {

    private val _uiState = MutableStateFlow(BalanceUiState())
    val uiState: StateFlow<BalanceUiState> = _uiState.asStateFlow()

    private val _settlements = MutableStateFlow<List<SettlementRecord>>(emptyList())
    val settlements: StateFlow<List<SettlementRecord>> = _settlements.asStateFlow()

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

    /**
     * Marks a single person's balance as settled and records a settlement entry.
     */
    fun settleUp(personId: String, method: String) {
        val current = _uiState.value
        val item = current.balances.find { it.id == personId } ?: return
        if (item.amount == 0.0) return

        val record = SettlementRecord(
            id = UUID.randomUUID().toString(),
            personId = item.id,
            personName = item.personName,
            amount = item.amount,
            method = method,
            timestampMillis = System.currentTimeMillis()
        )

        val updatedBalances = current.balances.map { balanceItem ->
            if (balanceItem.id == personId) {
                balanceItem.copy(amount = 0.0)
            } else {
                balanceItem
            }
        }

        _uiState.value = current.copy(balances = updatedBalances)
        _settlements.value = _settlements.value + record
    }
}

