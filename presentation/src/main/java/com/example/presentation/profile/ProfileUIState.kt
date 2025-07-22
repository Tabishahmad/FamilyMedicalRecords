package com.example.presentation.profile

import com.example.domain.model.UserProfile
import java.util.ArrayList

data class ProfileUIState(
    val userProfiles: List<UserProfile> = ArrayList<UserProfile>()
)