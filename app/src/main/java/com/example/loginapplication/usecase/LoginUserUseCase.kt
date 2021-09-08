package com.example.loginapplication.usecase

import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

open class LoginUserUseCase @Inject constructor(
    private val getUserByEmailUseCase: GetUserByEmailUseCase,
    private val addLoggedInEmailToDatastoreUseCase: AddLoggedInEmailToDatastoreUseCase
) {
    open suspend operator fun invoke(email: String, password: String) : Result {
        Timber.d("invoke: $email")
        try {
            val userDTO = getUserByEmailUseCase(email)
            if (userDTO.password != password) {
                Timber.e("LoginUserCase: failed, passwords do not match")
                return Result.Failure
            }
            addLoggedInEmailToDatastoreUseCase(email)
            Timber.d("Success")
            return Result.Success
        } catch (e : Exception) {
            Timber.e("LoginUserCase: failed, exception: ${e.message}")
            return Result.Failure
        }
    }

    sealed class Result {
        object Success: Result()
        object Failure: Result()
    }

}