package com.hendry.saku.ui.transactiondetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hendry.saku.data.model.Transaction
import com.hendry.saku.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionDetailUiState(
    val isLoading: Boolean = false,
    val transaction: Transaction? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(TransactionDetailUiState())

    val uiState =
        _uiState.asStateFlow()

    fun getTransactionDetail(transactionId: String) {
        viewModelScope.launch {
            try {
                _uiState.value =
                    TransactionDetailUiState(isLoading = true)

                val transaction =
                    repository.getTransactionById(transactionId)

                _uiState.value =
                    TransactionDetailUiState(
                        transaction = transaction
                    )

            } catch (e: Exception) {
                _uiState.value =
                    TransactionDetailUiState(
                        errorMessage = e.message
                            ?: "Gagal mengambil detail transaksi"
                    )
            }
        }
    }
}