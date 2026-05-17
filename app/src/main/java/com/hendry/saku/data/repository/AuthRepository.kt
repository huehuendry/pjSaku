package com.hendry.saku.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hendry.saku.data.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.google.firebase.firestore.toObject

class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    private fun generateAccountNumber(): String {
        return (1000000000L..9999999999L).random().toString()
    }

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
        name: String,
        email: String,
        password: String
    ) {

        val result = firebaseAuth
            .createUserWithEmailAndPassword(
                email,
                password
            )
            .await()

        val firebaseUser = result.user ?: return

        try {

            val user = User(
                uid = firebaseUser.uid,
                name = name,
                email = email,
                balance = 0L,
                accountNumber = generateAccountNumber()
            )

            firestore
                .collection("users")
                .document(firebaseUser.uid)
                .set(user)
                .await()

        } catch (e: Exception) {

            firebaseUser.delete().await()

            throw e
        }
    }

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun getCurrentUserEmail(): String? {
        return firebaseAuth.currentUser?.email
    }

    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    suspend fun getCurrentUserProfile(): User? {

        val uid = getCurrentUserId() ?: return null

        val document = firestore
            .collection("users")
            .document(uid)
            .get()
            .await()

        return document.toObject<User>()
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}