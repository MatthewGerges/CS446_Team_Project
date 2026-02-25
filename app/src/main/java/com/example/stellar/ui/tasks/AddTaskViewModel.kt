package com.example.stellar.ui.tasks

import androidx.lifecycle.ViewModel
import com.example.stellar.data.model.TaskPriority
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AddTaskUiState(
    val title: String = "",
    val assigneeOptions: List<String> = listOf("You", "Alice", "Bob", "Charlie", "Diana"),
    val selectedAssignee: String? = null,
    val dueDate: String = "",
    val priority: TaskPriority = TaskPriority.MEDIUM
)

class AddTaskViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AddTaskUiState())
    val uiState: StateFlow<AddTaskUiState> = _uiState.asStateFlow()

    fun setTitle(value: String) {
        _uiState.value = _uiState.value.copy(title = value)
    }

    fun setAssignee(value: String) {
        _uiState.value = _uiState.value.copy(selectedAssignee = value)
    }

    fun setDueDate(value: String) {
        _uiState.value = _uiState.value.copy(dueDate = value)
    }

    fun setDueDateFromMillis(millis: Long) {
        val formatter = DateTimeFormatter.ofPattern("MMM d")
        val localDate = Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        _uiState.value = _uiState.value.copy(dueDate = formatter.format(localDate))
    }

    fun setPriority(priority: TaskPriority) {
        _uiState.value = _uiState.value.copy(priority = priority)
    }

    fun canSave(): Boolean {
        val state = _uiState.value
        return state.title.isNotBlank() && state.selectedAssignee != null
    }

    fun save(onSaved: () -> Unit) {
        val state = _uiState.value
        val assignee = state.selectedAssignee ?: return
        if (state.title.isBlank()) return

        TaskRepository.addTask(
            title = state.title.trim(),
            assignee = assignee,
            dueDate = state.dueDate.trim(),
            priority = state.priority
        )

        onSaved()
    }
}

