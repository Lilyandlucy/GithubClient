package com.github.client.ui

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.client.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MainViewModel(
    private val loginUseCase: LoginUseCase, private val application: Application
) : ViewModel() {

    private val _networkAvailable = MutableLiveData<Boolean>(true)
    val networkAvailable: LiveData<Boolean> = _networkAvailable

    fun setNetworkAvailability(isAvailable: Boolean) {
        _networkAvailable.postValue(isAvailable)
    }

    fun handleOAuthCode(code: String) {
        viewModelScope.launch {
            loginUseCase(code).catch { e ->
                //TODO handle auth error
                e.printStackTrace()
            }.collect { success ->
                // show toast of auth result
                Toast.makeText(
                    application,
                    if (success) "Login Success" else "Login Failed", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
