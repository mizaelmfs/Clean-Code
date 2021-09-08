package com.example.loginapplication

import app.cash.turbine.test
import com.example.loginapplication.screens.login.LoginErrorType
import com.example.loginapplication.screens.login.LoginState
import com.example.loginapplication.screens.login.LoginViewModel
import com.example.loginapplication.usecase.GetForgotPasswordUseCase
import com.example.loginapplication.usecase.LoginUserUseCase
import com.example.loginapplication.usecase.RegisterUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class LoginViewModelTest {

    private lateinit var loginUserUseCase: LoginUserUseCase
    private lateinit var registerUserUseCase: RegisterUserUseCase
    private lateinit var getForgotPasswordUseCase: GetForgotPasswordUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun setupViewModel(
        loginUserUseCase: LoginUserUseCase = mock(LoginUserUseCase::class.java),
        registerUserUseCase: RegisterUserUseCase = mock(RegisterUserUseCase::class.java),
        getForgotPasswordUseCase: GetForgotPasswordUseCase = mock(GetForgotPasswordUseCase::class.java)
    ) : LoginViewModel {
        this.loginUserUseCase = loginUserUseCase
        this.registerUserUseCase = registerUserUseCase
        this.getForgotPasswordUseCase = getForgotPasswordUseCase

        return LoginViewModel(
            this.loginUserUseCase,
            this.registerUserUseCase,
            this.getForgotPasswordUseCase
        )
    }

    @Test
    fun loginClicked_validateInputsErrors() {
        val viewModel = setupViewModel()
        val invalidUsername = ""
        val invalidPassword = ""

        runBlockingTest {
            viewModel.state.test {
                assertEquals(LoginState(), awaitItem())
                viewModel.loginClicked(invalidUsername, invalidPassword)
                verify(loginUserUseCase, never()).invoke(invalidUsername, invalidPassword)
                assertEquals(LoginState(false, false), awaitItem())
            }
        }
    }

    @Test
    fun loginClicked_inputsGood_loginError() {
        val viewModel = setupViewModel(
            loginUserUseCase = mock(
                LoginUserUseCase::class.java
            ) {
                LoginUserUseCase.Result.Failure
            }
        )
        val username = "teste@teste.com"
        val password = "12345678"

        runBlockingTest {
            viewModel.error.test {
                viewModel.loginClicked(username, password)
                verify(loginUserUseCase, atLeastOnce()).invoke(username, password)
                assertEquals(LoginErrorType.LOGIN, awaitItem())
            }
        }
    }

    @Test
    fun loginClicked_inputs_loginSuccess() {
        val viewModel = setupViewModel(
            loginUserUseCase = mock(
                LoginUserUseCase::class.java
            ) {
                LoginUserUseCase.Result.Success
            }
        )
        val invalidUsername = ""
        val invalidPassword = ""
        val username = "teste@teste.com"
        val password = "12345678"

        runBlockingTest {
            viewModel.state.test {
                assertEquals(LoginState(), awaitItem())
                viewModel.loginClicked(invalidUsername, invalidPassword)
                assertEquals(LoginState(false, false), awaitItem())
                viewModel.loginClicked(username, password)
                assertEquals(LoginState(), awaitItem())
            }

            viewModel.error.test {
                viewModel.loginClicked(username, password)
                expectNoEvents()
            }

            viewModel.navigationToApp.test {
                viewModel.loginClicked(username, password)
                assertEquals(Unit, awaitItem())
            }

            verify(loginUserUseCase, atLeast(3)).invoke(username, password)
        }
    }

    @Test
    fun signupClicked_inputsGood_signupError() {
        val viewModel = setupViewModel(
            registerUserUseCase = mock(
                RegisterUserUseCase::class.java
            ) {
                RegisterUserUseCase.Result.Failure
            }
        )
        val username = "teste@teste.com"
        val password = "12345678"

        runBlockingTest {
            viewModel.error.test {
                viewModel.signUpClicked(username, password)
                verify(registerUserUseCase, atLeastOnce()).invoke(username, password)
                assertEquals(LoginErrorType.SIGNUP, awaitItem())
            }
        }
    }

    @Test
    fun signupClicked_inputsGood_registerSuccess() {
        val viewModel = setupViewModel(
            loginUserUseCase = mock(
                LoginUserUseCase::class.java
            ) {
                LoginUserUseCase.Result.Success
            },
            registerUserUseCase = mock(
                RegisterUserUseCase::class.java
            ) {
                RegisterUserUseCase.Result.Success
            }
        )
        val invalidUsername = ""
        val invalidPassword = ""
        val username = "teste@teste.com"
        val password = "12345678"

        runBlockingTest {
            viewModel.state.test {
                assertEquals(LoginState(), awaitItem())
                viewModel.signUpClicked(invalidUsername, invalidPassword)
                assertEquals(LoginState(false, false), awaitItem())
                viewModel.signUpClicked(username, password)
                assertEquals(LoginState(), awaitItem())
            }

            viewModel.error.test {
                viewModel.signUpClicked(username, password)
                expectNoEvents()
            }

            viewModel.registerSuccess.test {
                viewModel.signUpClicked(username, password)
                assertEquals(Unit, awaitItem())
            }
            verify(registerUserUseCase, atLeast(3)).invoke(username, password)
            verify(loginUserUseCase, atLeast(3)).invoke(username, password)
        }
    }

    @Test
    fun forgotPasswordClicked() {
        val viewModel = setupViewModel()
        runBlockingTest {
            viewModel.bottomSheetShow.test {
                viewModel.forgotPasswordClicked()
                assertEquals(Unit, awaitItem())
            }
        }
    }

    @Test
    fun forgotPasswordSubmitClicked_success() {
        val email = "teste@teste.com"
        val password = "12345678"

        val viewModel = setupViewModel(
            getForgotPasswordUseCase = mock(GetForgotPasswordUseCase::class.java) {
                GetForgotPasswordUseCase.Result.Success(password)
            }
        )

        runBlockingTest {
            viewModel.forgotPasswordGetSuccess.test {
                viewModel.forgotPasswordSubmitClicked(email)
                verify(getForgotPasswordUseCase, atLeastOnce()).invoke(email)
                assertEquals(password, awaitItem())
            }
        }
    }

    @Test
    fun forgotPasswordSubmitClicked_failure() {
        val email = "teste@teste.com"

        val viewModel = setupViewModel(
            getForgotPasswordUseCase = mock(GetForgotPasswordUseCase::class.java) {
                GetForgotPasswordUseCase.Result.Failure
            }
        )

        runBlockingTest {
            viewModel.error.test {
                viewModel.forgotPasswordSubmitClicked(email)
                verify(getForgotPasswordUseCase, atLeastOnce()).invoke(email)
                assertEquals(LoginErrorType.FORGOT_PASSWORD, awaitItem())
            }
        }
    }

    @Test
    fun onRegistrationSnackBarDismissed() {
        val viewModel = setupViewModel()

        runBlockingTest {
            viewModel.navigationToApp.test {
                viewModel.onRegistrationSnackbarDismissed()
                assertEquals(Unit, awaitItem())
            }
        }
    }
}