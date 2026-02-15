package com.example.stellar.ui.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.FamilyRestroom
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.School
import androidx.lifecycle.ViewModel
import com.example.stellar.data.model.Scenario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ProfileSelectionUiState(
    val profileName: String = "",
    val suggestedScenarios: List<Scenario> = listOf(
        Scenario("1", "Roommates", "Sharing an apartment with friends", Icons.Outlined.Group),
        Scenario("2", "Housemates", "Living in a shared house", Icons.Outlined.Apartment),
        Scenario("3", "Student Housing", "Dorm or student residence", Icons.Outlined.School),
        Scenario("4", "Family Home", "Living with family members", Icons.Outlined.FamilyRestroom)
    ),
    val selectedScenarioId: String? = null
)

class ProfileSelectionViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileSelectionUiState())
    val uiState: StateFlow<ProfileSelectionUiState> = _uiState.asStateFlow()

    fun onProfileNameChange(value: String) {
        _uiState.update { it.copy(profileName = value) }
    }

    fun onScenarioSelected(scenario: Scenario) {
        _uiState.update {
            it.copy(
                selectedScenarioId = scenario.id,
                profileName = scenario.name
            )
        }
    }
}
