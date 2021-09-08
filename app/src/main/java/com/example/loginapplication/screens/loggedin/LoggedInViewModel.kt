package com.example.loginapplication.screens.loggedin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginapplication.usecase.LogOutUseCase
import com.example.loginapplication.usecase.ObserveUserCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoggedInViewModel @Inject constructor(
    private val logOutUseCase: LogOutUseCase,
    private val observeUserCase: ObserveUserCase
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: Flow<String> = _email

    init {
        viewModelScope.launch {
            observeUserCase().onEach { user ->
                Timber.d(user.toString())
                user?.apply {
                    _email.emit(this.email)
                }
            }.launchIn(this)
        }
    }

    fun logOutClicked() {
        viewModelScope.launch {
            logOutUseCase.invoke()
            Timber.d("Logout clicked invoked")
        }
    }
}