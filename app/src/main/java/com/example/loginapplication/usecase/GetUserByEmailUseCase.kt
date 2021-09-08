package com.example.loginapplication.usecase

import com.example.loginapplication.model.db.dao.UserDAO
import com.example.loginapplication.model.db.dto.UserDTO
import timber.log.Timber
import javax.inject.Inject

open class GetUserByEmailUseCase @Inject constructor(
    private val userDAO: UserDAO
) {

    open suspend operator fun invoke(email: String) : UserDTO {
        Timber.d("invoke: $email")
        return userDAO.findByEmail(email)
    }
}