package com.example.data.mapper

import com.example.data.local.db.UserProfileEntity
import com.example.domain.model.UserProfile

// Domain → Room
fun UserProfile.toEntity(): UserProfileEntity {
    return UserProfileEntity(
        id = id.toIntOrNull() ?: 0, // Room auto-generates if id is 0
        name = name,
        age = age,
        gender = gender,
        notes = notes
    )
}

// Room → Domain
fun UserProfileEntity.toDomain(): UserProfile {
    return UserProfile(
        id = id.toString(),
        name = name,
        age = age,
        gender = gender,
        notes = notes
    )
}

