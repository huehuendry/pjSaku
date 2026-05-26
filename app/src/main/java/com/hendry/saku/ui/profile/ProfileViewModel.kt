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
    val errorMessage: String? = null
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
                _uiState.value = ProfileUiState(isLoading = true)

                val user = repository.getCurrentUserProfile()

                _uiState.value = ProfileUiState(user = user)

            } catch (e: Exception) {
                _uiState.value = ProfileUiState(
                    errorMessage = e.message ?: "Gagal mengambil data akun"
                )
            }
        }
    }

    fun logout() {
        repository.logout()
    }
}