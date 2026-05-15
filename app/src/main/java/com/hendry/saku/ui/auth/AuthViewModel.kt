package com.hendry.saku.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hendry.saku.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _uiState.value = AuthUiState(isLoading = true)

                repository.login(email, password)

                _uiState.value = AuthUiState(isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    errorMessage = e.message ?: "Login gagal"
                )
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            try {
                _uiState.value = AuthUiState(isLoading = true)

                repository.register(email, password)

                _uiState.value = AuthUiState(isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    errorMessage = e.message ?: "Register gagal"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState()
    }
}