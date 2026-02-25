package com.example.stellar.ui.tasks

import androidx.lifecycle.ViewModel
import com.example.stellar.data.model.Task
import com.example.stellar.data.model.TaskPriority
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlinx.coroutines.flow.StateFlow

enum class TaskFilter { ALL, MINE, UPCOMING, COMPLETED }

data class TaskScheduleUiState(
    val tasks: List<Task> = listOf(
        Task("1", "Buy groceries", "You", "Mar 16", priority = TaskPriority.HIGH),
        Task("2", "Do laundry", "Alice", "Feb 17", priority = TaskPriority.MEDIUM),
        Task("3", "Buy detergent", "You", "Mar 18", priority = TaskPriority.LOW),
        Task("4", "Clean kitchen", "Bob", "Feb 19", priority = TaskPriority.HIGH),
        Task("5", "Change bedsheets", "You", "Feb 20", priority = TaskPriority.MEDIUM),
        Task("6", "Take out trash", "Charlie", "Feb 15", isCompleted = true, priority = TaskPriority.LOW)
    ),
    val selectedFilter: TaskFilter = TaskFilter.ALL
) {
    private fun parseDueDate(dueDate: String): LocalDate? {
        if (dueDate.isBlank()) return null
        val formatter = DateTimeFormatter.ofPattern("MMM d")
        return try {
            // Interpret dates as occurring in the current year
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

    val filteredTasks: List<Task>
        get() = when (selectedFilter) {
            TaskFilter.ALL -> tasks
            TaskFilter.MINE -> tasks.filter { it.assignee == "You" }
            TaskFilter.UPCOMING -> tasks.filter { task ->
                !task.isCompleted && parseDueDate(task.dueDate)?.let { it >= today } != false
            }
            TaskFilter.COMPLETED -> tasks.filter { it.isCompleted }
        }
}

class TaskScheduleViewModel : ViewModel() {
    val uiState: StateFlow<TaskScheduleUiState> = TaskRepository.uiState

    fun onFilterChange(filter: TaskFilter) {
        TaskRepository.setFilter(filter)
    }

    fun onToggleComplete(taskId: String) {
        TaskRepository.toggleComplete(taskId)
    }
}
