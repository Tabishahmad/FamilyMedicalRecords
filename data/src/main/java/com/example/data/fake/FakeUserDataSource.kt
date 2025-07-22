package com.example.data.fake


import com.example.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class FakeUserDataSource {

    private val userProfiles = MutableStateFlow<List<UserProfile>>(emptyList())

    fun getAll(): Flow<List<UserProfile>> = userProfiles.asStateFlow()

    suspend fun add(profile: UserProfile) {
        val newProfile = profile.copy(id = UUID.randomUUID().toString())
        userProfiles.value = userProfiles.value + newProfile
    }
}
