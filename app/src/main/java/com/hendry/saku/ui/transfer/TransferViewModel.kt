package com.hendry.saku.ui.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hendry.saku.data.model.SavedRecipient
import com.hendry.saku.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransferUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val transactionId: String? = null,
    val errorMessage: String? = null,
    val savedRecipients: List<SavedRecipient> = emptyList()
)

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransferUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeSavedRecipients()
    }

    private fun observeSavedRecipients() {
        viewModelScope.launch {
            repository.observeSavedRecipients()
                .collect { recipients ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            savedRecipients = recipients
                        )
                    }
                }
        }
    }

    fun transfer(
        accountNumber: String,
        amountText: String,
        note: String
    ) {
        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        isLoading = true,
                        isSuccess = false,
                        transactionId = null,
                        errorMessage = null
                    )
                }

                val amount = amountText.toLongOrNull()

                if (accountNumber.isBlank()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Nomor rekening tujuan tidak boleh kosong"
                        )
                    }
                    return@launch
                }

                if (amount == null || amount <= 0L) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Nominal transfer tidak valid"
                        )
                    }
                    return@launch
                }

                val transactionId = repository.transferMoney(
                    receiverAccountNumber = accountNumber,
                    amount = amount,
                    note = note
                )

                runCatching {
                    repository.saveRecipientAfterTransfer(
                        receiverAccountNumber = accountNumber
                    )
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        transactionId = transactionId,
                        errorMessage = null
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = false,
                        transactionId = null,
                        errorMessage = e.message ?: "Transfer gagal"
                    )
                }
            }
        }
    }

    fun resetState() {
        _uiState.update {
            it.copy(
                isLoading = false,
                isSuccess = false,
                transactionId = null,
                errorMessage = null
            )
        }
    }
}