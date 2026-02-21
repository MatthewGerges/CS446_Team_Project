package com.example.stellar.ui.balance

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RoommateOption(val id: String, val name: String)

data class AddExpenseUiState(
    val amount: String = "",
    val description: String = "",
    val roommateOptions: List<RoommateOption> = listOf(
        RoommateOption("1", "Alice"),
        RoommateOption("2", "Bob"),
        RoommateOption("3", "Charlie"),
        RoommateOption("4", "Diana")
    ),
    val selectedIds: Set<String> = emptySet()
)

class AddExpenseViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AddExpenseUiState())
    val uiState: StateFlow<AddExpenseUiState> = _uiState.asStateFlow()

    fun setAmount(value: String) {
        _uiState.value = _uiState.value.copy(amount = value)
    }

    fun setDescription(value: String) {
        _uiState.value = _uiState.value.copy(description = value)
    }

    fun toggleRoommate(id: String) {
        val current = _uiState.value.selectedIds
        _uiState.value = _uiState.value.copy(
            selectedIds = if (id in current) current - id else current + id
        )
    }

    fun save(onSaved: () -> Unit) {
        val state = _uiState.value
        val amount = state.amount.toDoubleOrNull() ?: return
        if (state.selectedIds.isEmpty()) return
        // TODO: persist expense (e.g. call repository / BalanceViewModel)
        onSaved()
    }

    fun canSave(): Boolean {
        val state = _uiState.value
        return state.amount.toDoubleOrNull() != null &&
            state.amount.toDoubleOrNull()!! > 0 &&
            state.selectedIds.isNotEmpty()
    }
}