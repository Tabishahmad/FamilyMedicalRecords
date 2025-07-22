package com.example.domain.repository

import com.example.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun addUserProfile(profile: UserProfile)
    fun getAllProfiles(): Flow<List<UserProfile>>
    suspend fun deleteUserProfile(profile: UserProfile)
    suspend fun getUnSyncedProfiles(): List<UserProfile>
    suspend fun markAsSynced(id: Int)
    suspend fun syncUnSyncedProfilesToFirebase()
}
