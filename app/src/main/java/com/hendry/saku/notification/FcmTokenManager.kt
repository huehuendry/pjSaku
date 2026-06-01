package com.hendry.saku.notification

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.hendry.saku.data.remote.FirestoreCollection

object FcmTokenManager {

    fun syncTokenForCurrentUser() {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return

        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                FirebaseFirestore.getInstance()
                    .collection(FirestoreCollection.USERS)
                    .document(currentUser.uid)
                    .update(
                        mapOf(
                            "fcmToken" to token,
                            "fcmTokenUpdatedAt" to System.currentTimeMillis()
                        )
                    )
            }
    }
}