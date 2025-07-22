//package com.example.data.repository
//
//import com.example.domain.model.UserProfile
//import com.example.domain.repository.FirebaseRepository
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.FieldValue
//import kotlinx.coroutines.tasks.await
//
//class FirebaseRepositoryImpl(
//    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
//) : FirebaseRepository {
//
//    override suspend fun uploadUserProfile(profile: UserProfile) {
//        val data = mapOf(
//            "name" to profile.name,
//            "age" to profile.age,
//            "gender" to profile.gender,
//            "notes" to profile.notes,
//            "createdAt" to FieldValue.serverTimestamp()
//        )
//
//        firestore.collection("users")
//            .document(profile.id) // ID is string
//            .set(data)
//            .await() // ✅ coroutine-friendly
//    }
//}
