package com.example.stellar.ui.profile

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * In-memory store for the current user's profile until backend is integrated.
 */
data class UserProfile(
    val displayName: String = "User",
    val email: String = "",
    val phone: String = ""
)

object ProfileRepository {
    private val _profile = MutableStateFlow(UserProfile())
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    fun updateProfile(displayName: String, email: String, phone: String) {
        _profile.value = UserProfile(
            displayName = displayName.ifBlank { _profile.value.displayName },
            email = email.trim(),
            phone = phone.trim()
        )
    }

    /** Call after sign up / log in to set name and email from auth. */
    fun setFromSignup(name: String, email: String) {
        _profile.value = UserProfile(
            displayName = name.trim().ifBlank { email.trim().ifBlank { _profile.value.displayName } },
            email = email.trim().ifBlank { _profile.value.email },
            phone = _profile.value.phone
        )
    }

    /** Call after ProfileSelection "Create" to set initial display name (e.g. when no signup name). */
    fun setDisplayNameFromOnboarding(name: String) {
        if (name.isNotBlank()) {
            _profile.value = _profile.value.copy(displayName = name.trim())
        }
    }

    /** Stub: validate current password (no backend yet). Always returns true. */
    fun validatePassword(current: String): Boolean = true

    /** Stub: record password change (no backend yet). */
    fun changePassword(newPassword: String) {}
}
