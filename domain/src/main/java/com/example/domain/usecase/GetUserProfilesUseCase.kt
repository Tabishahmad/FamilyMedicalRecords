package com.example.domain.usecase

import com.example.domain.model.UserProfile
import com.example.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetUserProfilesUseCase(
    private val repository: UserRepository
) {
    operator fun invoke(): Flow<List<UserProfile>> {
        return repository.getAllProfiles()
    }
}
