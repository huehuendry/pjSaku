package com.hendry.saku.ui.topup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hendry.saku.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TopUpUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val transactionId: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class TopUpViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TopUpUiState())
    val uiState = _uiState.asStateFlow()

    fun topUp(
        amountText: String
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

                val amount = amountText
                    .replace(".", "")
                    .replace(",", "")
                    .trim()
                    .toLongOrNull()

                if (amount == null || amount <= 0L) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Nominal top up tidak valid"
                        )
                    }
                    return@launch
                }

                if (amount < 10_000L) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Minimal top up adalah Rp10.000"
                        )
                    }
                    return@launch
                }

                val transactionId = repository.topUpBalance(
                    amount = amount
                )

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
                        errorMessage = e.message ?: "Top up gagal"
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