package com.example.data.repository

import android.util.Log
import com.example.data.local.LocalUserDataSource
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.domain.model.UserProfile
import com.example.domain.repository.UserRepository
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(
    private val localDataSource: LocalUserDataSource,
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
) : UserRepository {

    override suspend fun addUserProfile(profile: UserProfile) {
        localDataSource.insert(profile.toEntity())
    }

    override fun getAllProfiles(): Flow<List<UserProfile>> {
        return localDataSource.getAll().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun deleteUserProfile(profile: UserProfile) {
        println("deleteUserProfile  "  + profile.id)
        localDataSource.delete(profile.toEntity())
    }

    override suspend fun getUnSyncedProfiles(): List<UserProfile> {
        return localDataSource
            .getUnSyncedUsers()              // returns List<UserProfileEntity>
            .map { it.toDomain() }           // convert to List<UserProfile>
    }

    override suspend fun markAsSynced(id: Int) {
        localDataSource.markAsSynced(id)     // simply delegate to local DAO
    }

    override suspend fun syncUnSyncedProfilesToFirebase() {
        val unsynced = localDataSource.getUnSyncedUsers()
        unsynced.forEach { user ->
            val userMap = mapOf(
                "name" to user.name,
                "age" to user.age,
                "gender" to user.gender,
                "notes" to user.notes
            )
            try {
                firebaseDatabase.reference
                    .child("users")
                    .child(user.id.toString())
                    .setValue(userMap)
                    .await()

                localDataSource.markAsSynced(user.id)
            } catch (e: Exception) {
                Log.e("UserRepositoryImpl", "Sync failed: ${e.message}")
            }
        }
    }

}
