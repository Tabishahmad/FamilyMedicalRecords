package com.example.presentation.profile


import  androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.UserProfile
import com.example.domain.usecase.AddUserProfileUseCase
import com.example.domain.usecase.DeleteUserProfileUseCase
import com.example.domain.usecase.GetUserProfilesUseCase
import com.example.domain.usecase.SyncUserProfilesUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UserViewModel(
    private val getUserProfilesUseCase: GetUserProfilesUseCase,
    private val addUserProfileUseCase: AddUserProfileUseCase,
    private val deleteUser: DeleteUserProfileUseCase,
    private val syncUserProfilesUseCase: SyncUserProfilesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUIState())
    val uiState: StateFlow<ProfileUIState> = _uiState.asStateFlow()


    init {
        getProfiles()
    }

    private fun getProfiles() {
        getUserProfilesUseCase()
            .onEach { profiles ->
                _uiState.update { it.copy(userProfiles = profiles) }
            }
            .launchIn(viewModelScope)
    }

    fun addProfile(profile: UserProfile) {
        viewModelScope.launch {
            addUserProfileUseCase(profile)
            syncUserProfilesUseCase()
        }
    }
    fun getProfileById(id: String): UserProfile? {
        return uiState.value.userProfiles.find { it.id == id }
    }
    fun deleteProfile(profile: UserProfile) {
        viewModelScope.launch {
            deleteUser(profile)
        }
    }
    fun syncProfilesOnResume() {
        viewModelScope.launch {
            syncUserProfilesUseCase()
        }
    }
}
