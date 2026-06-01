package com.hendry.saku.notification

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hendry.saku.data.remote.FirestoreCollection

class SakuFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        val currentUser = FirebaseAuth.getInstance().currentUser ?: return

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

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title
            ?: message.data["title"]
            ?: "Notifikasi Saku"

        val body = message.notification?.body
            ?: message.data["body"]
            ?: "Ada informasi transaksi terbaru."

        val transactionId = message.data["transactionId"]

        NotificationHelper.showTransactionNotification(
            context = this,
            title = title,
            message = body,
            transactionId = transactionId
        )
    }
}