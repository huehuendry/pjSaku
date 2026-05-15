package com.hendry.saku.ui.dashboard

import androidx.lifecycle.ViewModel
import com.hendry.saku.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    fun getUserEmail(): String {
        return repository.getCurrentUserEmail() ?: "User"
    }

    fun logout() {
        repository.logout()
    }
}