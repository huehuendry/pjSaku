package com.hendry.saku.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hendry.saku.data.model.Transaction
import com.hendry.saku.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val isLoading: Boolean = false,
    val transactions: List<Transaction> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getTransactions()
    }

    fun getTransactions() {
        viewModelScope.launch {
            try {
                _uiState.value = HistoryUiState(
                    isLoading = true
                )

                val transactions = repository.getAllTransactions()

                _uiState.value = HistoryUiState(
                    transactions = transactions
                )

            } catch (e: Exception) {
                _uiState.value = HistoryUiState(
                    errorMessage = e.message ?: "Gagal mengambil riwayat transaksi"
                )
            }
        }
    }
}