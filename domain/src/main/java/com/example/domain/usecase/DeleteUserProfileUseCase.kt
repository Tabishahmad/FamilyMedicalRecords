package com.example.domain.usecase

import com.example.domain.model.UserProfile
import com.example.domain.repository.UserRepository

class DeleteUserProfileUseCase(private val repo: UserRepository) {
    suspend operator fun invoke(profile: UserProfile) = repo.deleteUserProfile(profile)
}
