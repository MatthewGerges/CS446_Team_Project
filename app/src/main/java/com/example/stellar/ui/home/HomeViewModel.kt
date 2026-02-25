package com.example.stellar.ui.home

import androidx.lifecycle.ViewModel
import com.example.stellar.data.model.Receipt
import com.example.stellar.data.model.ReceiptStatus
import com.example.stellar.data.model.Task
import com.example.stellar.ui.balance.BalanceRepository
import com.example.stellar.ui.profile.ProfileRepository
import com.example.stellar.ui.tasks.TaskRepository
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val userName: String = "User",
    val totalOwed: Double = 0.0,
    val totalOwing: Double = 0.0,
    val upcomingTasks: List<Task> = emptyList(),
    val recentReceipts: List<Receipt> = listOf(
        Receipt("1", "Walmart", 67.43, "Feb 14", ReceiptStatus.CONFIRMED),
        Receipt("2", "Costco", 124.99, "Feb 12", ReceiptStatus.PENDING)
    )
)

class HomeViewModel : ViewModel() {
    private fun parseDueDate(dueDate: String): LocalDate? {
        if (dueDate.isBlank()) return null
        val formatter = DateTimeFormatter.ofPattern("MMM d")
        return try {
            val monthDay = LocalDate.parse("$dueDate ${LocalDate.now().year}", DateTimeFormatter.ofPattern("MMM d yyyy"))
            monthDay
        } catch (e: DateTimeParseException) {
            try {
                val parsed = formatter.parse(dueDate)
                LocalDate.of(
                    LocalDate.now().year,
                    parsed.get(java.time.temporal.ChronoField.MONTH_OF_YEAR),
                    parsed.get(java.time.temporal.ChronoField.DAY_OF_MONTH)
                )
            } catch (_: Exception) {
                null
            }
        }
    }

    private val today: LocalDate
        get() = LocalDate.now(ZoneId.systemDefault())

    val uiState: StateFlow<HomeUiState> = combine(
        ProfileRepository.profile,
        BalanceRepository.uiState,
        TaskRepository.uiState
    ) { profile, balanceState, taskState ->
        val totalOwed = balanceState.owedToYou.sumOf { it.amount }
        val totalOwing = kotlin.math.abs(balanceState.youOwe.sumOf { it.amount })
        
        val upcomingTasks = taskState.tasks.filter { task ->
            !task.isCompleted && parseDueDate(task.dueDate)?.let { it >= today } != false
        }.take(3) // Show up to 3 upcoming tasks
        
        HomeUiState(
            userName = profile.displayName,
            totalOwed = totalOwed,
            totalOwing = totalOwing,
            upcomingTasks = upcomingTasks,
            recentReceipts = HomeUiState().recentReceipts // Keep receipts as-is for now
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = run {
            val balanceState = BalanceRepository.uiState.value
            val taskState = TaskRepository.uiState.value
            val todayVal = LocalDate.now(ZoneId.systemDefault())
            HomeUiState(
                userName = ProfileRepository.profile.value.displayName,
                totalOwed = balanceState.owedToYou.sumOf { it.amount },
                totalOwing = kotlin.math.abs(balanceState.youOwe.sumOf { it.amount }),
                upcomingTasks = taskState.tasks.filter { task ->
                    !task.isCompleted && parseDueDate(task.dueDate)?.let { it >= todayVal } != false
                }.take(3)
            )
        }
    )
}
