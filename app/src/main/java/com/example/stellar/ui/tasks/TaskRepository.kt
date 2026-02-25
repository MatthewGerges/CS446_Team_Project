package com.example.stellar.ui.tasks

import com.example.stellar.data.model.Task
import com.example.stellar.data.model.TaskPriority
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Temporary in-memory store for task data until the backend is integrated.
 */
object TaskRepository {

    private val _uiState = MutableStateFlow(TaskScheduleUiState())
    val uiState: StateFlow<TaskScheduleUiState> = _uiState.asStateFlow()

    fun setFilter(filter: TaskFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun toggleComplete(taskId: String) {
        _uiState.update { state ->
            state.copy(
                tasks = state.tasks.map { task ->
                    if (task.id == taskId) task.copy(isCompleted = !task.isCompleted)
                    else task
                }
            )
        }
    }

    fun addTask(
        title: String,
        assignee: String,
        dueDate: String,
        priority: TaskPriority
    ) {
        val newTask = Task(
            id = UUID.randomUUID().toString(),
            title = title,
            assignee = assignee,
            dueDate = dueDate,
            isCompleted = false,
            priority = priority
        )

        _uiState.update { state ->
            state.copy(tasks = state.tasks + newTask)
        }
    }
}

