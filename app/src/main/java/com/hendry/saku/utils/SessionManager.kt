package com.hendry.saku.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {

    private val _sessionState = MutableStateFlow(SessionState.ACTIVE)
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    private var timerJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val TIMEOUT_MILLISECONDS = 10 * 1000L

    fun startSession() {
        resetTimer()
    }

    fun resetTimer() {
        timerJob?.cancel()
        _sessionState.value = SessionState.ACTIVE

        timerJob = coroutineScope.launch {
            delay(TIMEOUT_MILLISECONDS)
            _sessionState.value = SessionState.TIMEOUT
        }
    }

    fun stopSession() {
        timerJob?.cancel()
        _sessionState.value = SessionState.ACTIVE
    }
}

enum class SessionState {
    ACTIVE,
    TIMEOUT
}