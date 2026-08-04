package com.hendry.saku.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hendry.saku.data.model.Transaction
import com.hendry.saku.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// --- Filter Category ---
enum class TransactionFilter(val label: String) {
    ALL("Semua"),
    TRANSFER_IN("Transfer Masuk"),
    TRANSFER_OUT("Transfer Keluar"),
    TOP_UP("Top Up")
}

// --- UI State ---
data class HistoryUiState(
    val isLoading: Boolean = false,
    val transactions: List<Transaction> = emptyList(),
    val errorMessage: String? = null,
    val activeFilter: TransactionFilter = TransactionFilter.ALL
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _allTransactions = MutableStateFlow<List<Transaction>>(emptyList())

    private val _activeFilter = MutableStateFlow(TransactionFilter.ALL)
    val activeFilter = _activeFilter.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    
    val uiState = combine(
        _allTransactions,
        _activeFilter,
        _isLoading,
        _errorMessage
    ) { allTransactions, filter, isLoading, errorMessage ->

        val filtered = when (filter) {
            TransactionFilter.ALL         -> allTransactions
            TransactionFilter.TRANSFER_IN -> allTransactions.filter { it.type == "TRANSFER_IN" }
            TransactionFilter.TRANSFER_OUT-> allTransactions.filter { it.type == "TRANSFER_OUT" }
            TransactionFilter.TOP_UP      -> allTransactions.filter { it.type == "TOP_UP" }
        }

        HistoryUiState(
            isLoading = isLoading,
            transactions = filtered,
            errorMessage = errorMessage,
            activeFilter = filter
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HistoryUiState(isLoading = true)
    )

    init {
        loadTransactions()
    }

    fun loadTransactions() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val transactions = repository.getAllTransactions()

                _allTransactions.value = transactions
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Gagal mengambil riwayat transaksi"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setFilter(filter: TransactionFilter) {
        _activeFilter.value = filter
    }
}