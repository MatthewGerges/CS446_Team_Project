package com.example.stellar.ui.tasks

import androidx.lifecycle.ViewModel
import com.example.stellar.data.model.Task
import com.example.stellar.data.model.TaskPriority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class TaskFilter { ALL, MINE, UPCOMING, COMPLETED }

data class TaskScheduleUiState(
    val tasks: List<Task> = listOf(
        Task("1", "Buy groceries", "You", "Feb 16", priority = TaskPriority.HIGH),
        Task("2", "Do laundry", "Alice", "Feb 17", priority = TaskPriority.MEDIUM),
        Task("3", "Buy detergent", "You", "Feb 18", priority = TaskPriority.LOW),
        Task("4", "Clean kitchen", "Bob", "Feb 19", priority = TaskPriority.HIGH),
        Task("5", "Change bedsheets", "You", "Feb 20", priority = TaskPriority.MEDIUM),
        Task("6", "Take out trash", "Charlie", "Feb 15", isCompleted = true, priority = TaskPriority.LOW)
    ),
    val selectedFilter: TaskFilter = TaskFilter.ALL
) {
    val filteredTasks: List<Task>
        get() = when (selectedFilter) {
            TaskFilter.ALL -> tasks
            TaskFilter.MINE -> tasks.filter { it.assignee == "You" }
            TaskFilter.UPCOMING -> tasks.filter { !it.isCompleted }
            TaskFilter.COMPLETED -> tasks.filter { it.isCompleted }
        }
}

class TaskScheduleViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TaskScheduleUiState())
    val uiState: StateFlow<TaskScheduleUiState> = _uiState.asStateFlow()

    fun onFilterChange(filter: TaskFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun onToggleComplete(taskId: String) {
        _uiState.update { state ->
            state.copy(
                tasks = state.tasks.map { task ->
                    if (task.id == taskId) task.copy(isCompleted = !task.isCompleted)
                    else task
                }
            )
        }
    }
}
