package com.example.data.sync

import android.util.Log
import com.example.domain.repository.UserRepository
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class FirebaseSyncManager(
    private val userRepository: UserRepository,
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
) {
    suspend fun syncUnSyncedUsers() {
        val unsynced = userRepository.getUnSyncedProfiles()

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
                    .child(user.id)
                    .setValue(userMap)
                    .await()

                userRepository.markAsSynced(user.id.toInt())
            } catch (e: Exception) {
                Log.e("FirebaseSync", "Failed to sync user ${user.id}: ${e.message}")
            }
        }
    }
}
