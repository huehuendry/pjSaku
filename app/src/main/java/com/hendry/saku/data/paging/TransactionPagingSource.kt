package com.hendry.saku.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.hendry.saku.data.model.Transaction
import com.hendry.saku.data.remote.FirestoreCollection
import com.hendry.saku.ui.history.TransactionFilter
import kotlinx.coroutines.tasks.await

class TransactionPagingSource(
    private val firestore: FirebaseFirestore,
    private val userId: String,
    private val filter: TransactionFilter
) : PagingSource<DocumentSnapshot, Transaction>() {

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, Transaction>): DocumentSnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, Transaction> {
        return try {
            var query = firestore
                .collection(FirestoreCollection.TRANSACTIONS)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)

            if (filter != TransactionFilter.ALL) {
                val typeValue = when (filter) {
                    TransactionFilter.TRANSFER_IN  -> "TRANSFER_IN"
                    TransactionFilter.TRANSFER_OUT -> "TRANSFER_OUT"
                    TransactionFilter.TOP_UP       -> "TOP_UP"
                    TransactionFilter.ALL          -> ""
                }
                query = query.whereEqualTo("type", typeValue)
            }

            query = query.limit(params.loadSize.toLong())

            val afterDocument = params.key
            if (afterDocument != null) {
                query = query.startAfter(afterDocument)
            }

            val snapshot = query.get().await()
            val transactions = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Transaction::class.java)
            }

            val nextKey = if (snapshot.documents.size < params.loadSize) {
                null
            } else {
                snapshot.documents.lastOrNull()
            }

            LoadResult.Page(
                data = transactions,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
