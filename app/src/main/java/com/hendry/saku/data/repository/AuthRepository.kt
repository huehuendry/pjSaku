package com.hendry.saku.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hendry.saku.data.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.google.firebase.firestore.toObject
import com.hendry.saku.data.model.Transaction
import com.hendry.saku.data.remote.FirestoreCollection

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