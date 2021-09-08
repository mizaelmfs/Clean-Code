package com.example.loginapplication.usecase

import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

open class GetForgotPasswordUseCase @Inject constructor(
    private val getUserByEmailUseCase: GetUserByEmailUseCase
){

    open suspend operator fun invoke(email: String) : Result {
        Timber.d(email)
        return try {
            Result.Success(getUserByEmailUseCase(email).password)
        } catch (e: Exception) {
            Result.Failure
        }
    }

    sealed class Result {
        data class Success(val password: String) : Result()
        object Failure : Result()
    }
}