package com.hendry.saku.ui.splash

import androidx.lifecycle.ViewModel
import com.hendry.saku.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    fun isUserLoggedIn(): Boolean {
        return repository.isUserLoggedIn()
    }
}