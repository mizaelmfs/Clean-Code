package com.example.loginapplication.usecase

import com.example.loginapplication.model.db.dao.UserDAO
import timber.log.Timber
import javax.inject.Inject

class CheckIfUserExistsUseCase @Inject constructor(
    private val userDAO: UserDAO
)  {

    suspend operator fun invoke(email: String) : Boolean {
        Timber.d("invoke: $email")
        return userDAO.isRowIsExist(email)
    }
}