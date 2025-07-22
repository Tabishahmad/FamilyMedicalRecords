package com.example.domain.usecase

import com.example.domain.model.UserProfile
import com.example.domain.repository.UserRepository

class AddUserProfileUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(profile: UserProfile) {
        repository.addUserProfile(profile)
    }
}
