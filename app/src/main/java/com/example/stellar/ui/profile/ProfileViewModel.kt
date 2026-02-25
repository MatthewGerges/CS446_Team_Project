package com.example.stellar.ui.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel : ViewModel() {
    val profile: StateFlow<UserProfile> = ProfileRepository.profile
    val households: StateFlow<List<Household>> = HouseholdRepository.households
    val activeHouseholdId: StateFlow<String?> = HouseholdRepository.activeHouseholdId

    fun addHousehold(name: String): Household? = HouseholdRepository.addHousehold(name)
    fun setActiveHousehold(id: String) = HouseholdRepository.setActiveHousehold(id)
}
