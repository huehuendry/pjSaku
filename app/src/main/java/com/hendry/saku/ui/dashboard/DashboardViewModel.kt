package com.hendry.saku.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hendry.saku.data.model.User
import com.hendry.saku.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.hendry.saku.data.model.Transaction

data class DashboardUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val transactions: List<Transaction> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(DashboardUiState())

    val uiState =
        _uiState.asStateFlow()

    init {
        getUserProfile()
    }

    private fun getUserProfile() {

        viewModelScope.launch {
            try {

                _uiState.value =
                    DashboardUiState(
                        isLoading = true
                    )

                val user =
                    repository.getCurrentUserProfile()
                val transactions =
                    repository.getRecentTransactions()

                _uiState.value = DashboardUiState(
                    user = user,
                    transactions = transactions
                )

            } catch (e: Exception) {

                _uiState.value =
                    DashboardUiState(
                        errorMessage =
                        e.message
                    )
            }
        }
    }

    fun logout() {
        repository.logout()
    }
}