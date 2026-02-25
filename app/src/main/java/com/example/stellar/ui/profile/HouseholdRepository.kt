package com.example.stellar.ui.profile

import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Household(
    val id: String,
    val name: String
)

/**
 * In-memory store for the user's households until backend is integrated.
 */
object HouseholdRepository {
    private val _households = MutableStateFlow<List<Household>>(emptyList())
    private val _activeHouseholdId = MutableStateFlow<String?>(null)

    val households: StateFlow<List<Household>> = _households.asStateFlow()
    val activeHouseholdId: StateFlow<String?> = _activeHouseholdId.asStateFlow()

    val activeHousehold: Household?
        get() = _activeHouseholdId.value?.let { id -> _households.value.find { it.id == id } }

    fun addHousehold(name: String): Household? {
        val trimmed = name.trim().ifBlank { return null }
        val household = Household(UUID.randomUUID().toString(), trimmed)
        _households.value = _households.value + household
        if (_activeHouseholdId.value == null) {
            _activeHouseholdId.value = household.id
        }
        return household
    }

    fun setActiveHousehold(id: String) {
        if (_households.value.any { it.id == id }) {
            _activeHouseholdId.value = id
        }
    }

    /** Call after ProfileSelection "Create" to set the first household. */
    fun setFirstHousehold(name: String) {
        val trimmed = name.trim().ifBlank { "Home" }
        val household = Household(UUID.randomUUID().toString(), trimmed)
        _households.value = listOf(household)
        _activeHouseholdId.value = household.id
    }
}
