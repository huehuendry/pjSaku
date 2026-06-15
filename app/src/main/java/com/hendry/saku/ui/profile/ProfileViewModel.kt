package com.hendry.saku.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hendry.saku.data.model.User
import com.hendry.saku.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val errorMessage: String? = null,
    val isAccountDeleted: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getProfile()
    }

    private fun getProfile() {
        viewModelScope.launch {
            try {
                _uiState.value = ProfileUiState(
                    isLoading = true
                )

                val user = repository.getCurrentUserProfile()

                _uiState.value = ProfileUiState(
                    user = user
                )

            } catch (e: Exception) {
                _uiState.value = ProfileUiState(
                    errorMessage = e.message ?: "Gagal mengambil data akun"
                )
            }
        }
    }

    fun deleteAccount(
        password: String
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )

                repository.deleteAccount(
                    password = password
                )

                _uiState.value = ProfileUiState(
                    isAccountDeleted = true
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = when {
                        e.message?.contains("password is invalid", ignoreCase = true) == true ->
                            "Password yang kamu masukkan salah"

                        e.message?.contains("requires recent login", ignoreCase = true) == true ->
                            "Sesi login sudah terlalu lama. Silakan logout dan login ulang, lalu coba lagi"

                        else ->
                            e.message ?: "Gagal menghapus akun"
                    }
                )
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }

    fun logout() {
        repository.logout()
    }
}