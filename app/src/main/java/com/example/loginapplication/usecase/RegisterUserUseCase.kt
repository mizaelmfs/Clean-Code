package com.example.loginapplication.usecase

import com.example.loginapplication.model.db.dto.UserDTO
import timber.log.Timber
import javax.inject.Inject

open class RegisterUserUseCase @Inject constructor(
    private val addUserToDatabaseUseCase: AddUserToDatabaseUseCase,
    private val checkIfUserExistsUseCase: CheckIfUserExistsUseCase
) {

    open suspend operator fun invoke(email: String, password: String) : Result {
        Timber.d("invoke: $email")
        val userExists = checkIfUserExistsUseCase(email)
        return if (!userExists) {
            addUserToDatabaseUseCase(UserDTO(email = email, password = password))
            Timber.d("Success!")
            Result.Success
        } else {
            Timber.e("Failure!")
            Result.Failure
        }
    }

    sealed class Result {
        object Success: Result()
        object Failure: Result()
    }
}