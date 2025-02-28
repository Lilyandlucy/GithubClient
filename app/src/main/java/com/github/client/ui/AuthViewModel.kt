package com.github.client.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.client.domain.usecase.CheckAuthStatusUseCase
import com.github.client.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val checkAuthStatusUseCase: CheckAuthStatusUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            checkAuthStatusUseCase().collect { isAuthenticated ->
                _isLoggedIn.value = isAuthenticated
            }
        }
    }

    // 从 Profile 中迁移 方便首页读取状态
    fun logout() {
        viewModelScope.launch {
            logoutUseCase().collect { success ->
                if (success) {
                    _isLoggedIn.value = false
                }
            }
        }
    }
}