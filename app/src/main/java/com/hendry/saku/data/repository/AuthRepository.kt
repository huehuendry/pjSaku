package com.hendry.saku.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import com.hendry.saku.data.model.SavedRecipient
import com.hendry.saku.data.model.Transaction
import com.hendry.saku.data.model.User
import com.hendry.saku.data.remote.FirestoreCollection
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

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
                .collection(FirestoreCollection.USERS)
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
            .collection(FirestoreCollection.USERS)
            .document(uid)
            .get()
            .await()

        return document.toObject<User>()
    }

    suspend fun getRecentTransactions(): List<Transaction> {

        val uid = getCurrentUserId() ?: return emptyList()

        val snapshot = firestore
            .collection(FirestoreCollection.TRANSACTIONS)
            .whereEqualTo("userId", uid)
            .limit(5)
            .get()
            .await()

        return snapshot.documents.mapNotNull { document ->
            document.toObject(Transaction::class.java)
        }
    }

    fun observeSavedRecipients(): Flow<List<SavedRecipient>> = callbackFlow {
        val currentUserId = getCurrentUserId()

        if (currentUserId.isNullOrBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore
            .collection(FirestoreCollection.USERS)
            .document(currentUserId)
            .collection(FirestoreCollection.SAVED_RECIPIENTS)
            .orderBy("lastTransferAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val recipients = snapshot
                    ?.documents
                    ?.mapNotNull { document ->
                        document.toObject(SavedRecipient::class.java)
                    }
                    .orEmpty()

                trySend(recipients)
            }

        awaitClose {
            listener.remove()
        }
    }

    suspend fun saveRecipientAfterTransfer(
        receiverAccountNumber: String
    ) {
        val currentUserId = getCurrentUserId()
            ?: throw Exception("User belum login")

        val receiverQuery = firestore
            .collection(FirestoreCollection.USERS)
            .whereEqualTo("accountNumber", receiverAccountNumber)
            .limit(1)
            .get()
            .await()

        val receiverDoc = receiverQuery.documents.firstOrNull()
            ?: throw Exception("Nomor rekening tujuan tidak ditemukan")

        val recipientUid = receiverDoc.getString("uid") ?: receiverDoc.id
        val recipientName = receiverDoc.getString("name") ?: "Pengguna Saku"
        val recipientAccountNumber = receiverDoc.getString("accountNumber") ?: receiverAccountNumber

        val savedRecipient = SavedRecipient(
            recipientUid = recipientUid,
            recipientName = recipientName,
            recipientAccountNumber = recipientAccountNumber,
            lastTransferAt = Timestamp.now()
        )

        firestore
            .collection(FirestoreCollection.USERS)
            .document(currentUserId)
            .collection(FirestoreCollection.SAVED_RECIPIENTS)
            .document(recipientAccountNumber)
            .set(savedRecipient, SetOptions.merge())
            .await()
    }

    suspend fun transferMoney(
        receiverAccountNumber: String,
        amount: Long,
        note: String
    ): String {

        val senderUid = getCurrentUserId()
            ?: throw Exception("User belum login")

        val senderRef = firestore
            .collection(FirestoreCollection.USERS)
            .document(senderUid)

        val receiverQuery = firestore
            .collection(FirestoreCollection.USERS)
            .whereEqualTo("accountNumber", receiverAccountNumber)
            .limit(1)
            .get()
            .await()

        val receiverDoc = receiverQuery.documents.firstOrNull()
            ?: throw Exception("Nomor rekening tujuan tidak ditemukan")

        val receiverRef = receiverDoc.reference

        val senderTransactionRef =
            firestore.collection(FirestoreCollection.TRANSACTIONS).document()

        val receiverTransactionRef =
            firestore.collection(FirestoreCollection.TRANSACTIONS).document()

        firestore.runTransaction { transaction ->

            val senderSnapshot = transaction.get(senderRef)
            val receiverSnapshot = transaction.get(receiverRef)

            val senderAccountNumber =
                senderSnapshot.getString("accountNumber") ?: "-"

            val receiverAccountNumberValue =
                receiverSnapshot.getString("accountNumber") ?: receiverAccountNumber

            val senderBalance =
                senderSnapshot.getLong("balance") ?: 0L

            val receiverBalance =
                receiverSnapshot.getLong("balance") ?: 0L

            if (senderBalance < amount) {
                throw Exception("Saldo tidak mencukupi")
            }

            transaction.update(senderRef, "balance", senderBalance - amount)
            transaction.update(receiverRef, "balance", receiverBalance + amount)

            val senderTransaction = Transaction(
                id = senderTransactionRef.id,
                userId = senderUid,
                type = "TRANSFER_OUT",
                title = "Transfer Keluar",
                description = "Transfer ke $receiverAccountNumberValue",
                amount = amount,
                senderAccountNumber = senderAccountNumber,
                receiverAccountNumber = receiverAccountNumberValue,
                note = note,
                createdAt = System.currentTimeMillis()
            )

            val receiverTransaction = Transaction(
                id = receiverTransactionRef.id,
                userId = receiverDoc.id,
                type = "TRANSFER_IN",
                title = "Transfer Masuk",
                description = "Transfer dari $senderAccountNumber",
                amount = amount,
                senderAccountNumber = senderAccountNumber,
                receiverAccountNumber = receiverAccountNumberValue,
                note = note,
                createdAt = System.currentTimeMillis()
            )

            transaction.set(senderTransactionRef, senderTransaction)
            transaction.set(receiverTransactionRef, receiverTransaction)
        }.await()

        return senderTransactionRef.id
    }

    suspend fun topUpBalance(
        amount: Long
    ): String {

        val uid = getCurrentUserId()
            ?: throw Exception("User belum login")

        val userRef = firestore
            .collection(FirestoreCollection.USERS)
            .document(uid)

        val topUpTransactionRef =
            firestore.collection(FirestoreCollection.TRANSACTIONS).document()

        firestore.runTransaction { transaction ->

            val userSnapshot = transaction.get(userRef)

            val currentBalance =
                userSnapshot.getLong("balance") ?: 0L

            val accountNumber =
                userSnapshot.getString("accountNumber") ?: "-"

            transaction.update(
                userRef,
                "balance",
                currentBalance + amount
            )

            val topUpTransaction = Transaction(
                id = topUpTransactionRef.id,
                userId = uid,
                type = "TOP_UP",
                title = "Top Up",
                description = "Isi saldo Saku",
                amount = amount,
                senderAccountNumber = "-",
                receiverAccountNumber = accountNumber,
                note = "Top up saldo",
                createdAt = System.currentTimeMillis()
            )

            transaction.set(
                topUpTransactionRef,
                topUpTransaction
            )
        }.await()

        return topUpTransactionRef.id
    }

    suspend fun getAllTransactions(): List<Transaction> {

        val uid = getCurrentUserId() ?: return emptyList()

        val snapshot = firestore
            .collection(FirestoreCollection.TRANSACTIONS)
            .whereEqualTo("userId", uid)
            .get()
            .await()

        return snapshot.documents
            .mapNotNull { document ->
                document.toObject(Transaction::class.java)
            }
            .sortedByDescending { transaction ->
                transaction.createdAt
            }
    }

    suspend fun getTransactionById(
        transactionId: String
    ): Transaction? {

        val document = firestore
            .collection(FirestoreCollection.TRANSACTIONS)
            .document(transactionId)
            .get()
            .await()

        return document.toObject(Transaction::class.java)
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}