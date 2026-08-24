package com.hendry.saku.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hendry.saku.data.model.Transaction
import com.hendry.saku.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

enum class TransactionFilter(val label: String) {
    ALL("Semua"),
    TRANSFER_IN("Transfer Masuk"),
    TRANSFER_OUT("Transfer Keluar"),
    TOP_UP("Top Up")
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _activeFilter = MutableStateFlow(TransactionFilter.ALL)
    val activeFilter = _activeFilter.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactionsPagingFlow: Flow<PagingData<Transaction>> = _activeFilter
        .flatMapLatest { filter ->
            repository.getTransactionsPagingFlow(filter)
        }
        .cachedIn(viewModelScope)

    fun setFilter(filter: TransactionFilter) {
        _activeFilter.value = filter
    }
}