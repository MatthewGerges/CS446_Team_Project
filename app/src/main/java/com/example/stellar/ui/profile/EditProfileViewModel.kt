package com.example.stellar.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class EditProfileUiState(
    val displayName: String = "",
    val email: String = "",
    val phone: String = ""
)

class EditProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val current = ProfileRepository.profile.first()
            _uiState.value = EditProfileUiState(
                displayName = current.displayName,
                email = current.email,
                phone = current.phone
            )
        }
    }

    fun setDisplayName(value: String) {
        _uiState.value = _uiState.value.copy(displayName = value)
    }

    fun setEmail(value: String) {
        _uiState.value = _uiState.value.copy(email = value)
    }

    fun setPhone(value: String) {
        _uiState.value = _uiState.value.copy(phone = value)
    }

    fun save(onSaved: () -> Unit) {
        val state = _uiState.value
        ProfileRepository.updateProfile(
            displayName = state.displayName.trim().ifBlank { "User" },
            email = state.email,
            phone = state.phone
        )
        onSaved()
    }

    fun canSave(): Boolean = _uiState.value.displayName.isNotBlank()
}
