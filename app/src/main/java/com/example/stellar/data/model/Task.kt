package com.example.stellar.data.model

enum class TaskPriority { LOW, MEDIUM, HIGH }

data class Task(
    val id: String,
    val title: String,
    val assignee: String,
    val dueDate: String,
    val isCompleted: Boolean = false,
    val priority: TaskPriority = TaskPriority.MEDIUM
)
