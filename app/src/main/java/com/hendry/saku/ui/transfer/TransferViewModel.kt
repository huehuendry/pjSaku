package com.hendry.saku.ui.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hendry.saku.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransferUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val transactionId: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransferUiState())
    val uiState = _uiState.asStateFlow()

    fun transfer(
        accountNumber: String,
        amountText: String,
        note: String
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = TransferUiState(isLoading = true)

                val amount = amountText.toLongOrNull()

                if (accountNumber.isBlank()) {
                    _uiState.value = TransferUiState(
                        errorMessage = "Nomor rekening tujuan tidak boleh kosong"
                    )
                    return@launch
                }

                if (amount == null || amount <= 0L) {
                    _uiState.value = TransferUiState(
                        errorMessage = "Nominal transfer tidak valid"
                    )
                    return@launch
                }

                val transactionId =
                    repository.transferMoney(
                        receiverAccountNumber = accountNumber,
                        amount = amount,
                        note = note
                    )

                _uiState.value = TransferUiState(
                    isSuccess = true,
                    transactionId = transactionId
                )

            } catch (e: Exception) {
                _uiState.value = TransferUiState(
                    errorMessage = e.message ?: "Transfer gagal"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = TransferUiState()
    }
}