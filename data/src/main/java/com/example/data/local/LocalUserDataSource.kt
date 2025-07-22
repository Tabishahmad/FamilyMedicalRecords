package com.example.data.local

import com.example.data.local.db.UserProfileDao
import com.example.data.local.db.UserProfileEntity
import kotlinx.coroutines.flow.Flow

class LocalUserDataSource(
    private val dao: UserProfileDao
) {
    suspend fun insert(profile: UserProfileEntity) = dao.insertProfile(profile)
    fun getAll(): Flow<List<UserProfileEntity>> = dao.getAllProfiles()
    suspend fun delete(profile: UserProfileEntity) = dao.deleteProfile(profile)
    suspend fun getUnSyncedUsers(): List<UserProfileEntity> = dao.getUnSyncedUsers()
    suspend fun markAsSynced(id: Int) = dao.markAsSynced(id)
}
