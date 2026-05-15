package com.hendry.saku.data.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    suspend fun login(
        email: String,
        password: String
    ) {
        firebaseAuth
            .signInWithEmailAndPassword(
                email,
                password
            )
            .await()
    }

    suspend fun register(
        email: String,
        password: String
    ) {
        firebaseAuth
            .createUserWithEmailAndPassword(
                email,
                password
            )
            .await()
    }

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun getCurrentUserEmail(): String? {
        return firebaseAuth.currentUser?.email
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}