package com.example.stellar.ui.home

import androidx.lifecycle.ViewModel
import com.example.stellar.data.model.Receipt
import com.example.stellar.data.model.ReceiptStatus
import com.example.stellar.data.model.Task
import com.example.stellar.data.model.TaskPriority
import com.example.stellar.ui.profile.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val userName: String = "User",
    val totalOwed: Double = 47.75,
    val totalOwing: Double = 25.50,
    val upcomingTasks: List<Task> = listOf(
        Task("1", "Buy groceries", "You", "Feb 16", priority = TaskPriority.HIGH),
        Task("2", "Do laundry", "Alice", "Feb 17", priority = TaskPriority.MEDIUM),
        Task("3", "Buy detergent", "Bob", "Feb 18", priority = TaskPriority.LOW)
    ),
    val recentReceipts: List<Receipt> = listOf(
        Receipt("1", "Walmart", 67.43, "Feb 14", ReceiptStatus.CONFIRMED),
        Receipt("2", "Costco", 124.99, "Feb 12", ReceiptStatus.PENDING)
    )
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = combine(
        _uiState,
        ProfileRepository.profile
    ) { state, profile ->
        state.copy(userName = profile.displayName)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _uiState.value.copy(userName = ProfileRepository.profile.value.displayName)
    )
}
