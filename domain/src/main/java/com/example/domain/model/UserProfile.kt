package com.example.domain.model

data class UserProfile(
    val id: String = "",
    val name: String,
    val age: Int,
    val gender: String,
    val notes: String? = null
)